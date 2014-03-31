/* 
 * Copyright (C) 2001 by Dave Jarvis
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA	02111-1307, USA.
 * Online at: http://www.gnu.org/copyleft/gpl.html
 */

//package com.joot.jigo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import java.util.Observable;
import java.util.Observer;

/**
 * The Goban knows about stones and board markup, and is responsible for
 * displaying itself.
 * <P>
 * A blank board, when drawn, will look something like this:
 *
 * <PRE>
 * (0, 0)
 *    +-------------------------------+
 *    | +-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |
 *    | | | | | | | | | | | | | | | |{----- Goban
 *    | +-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |
 *    | | | | | | | | | | | | | | | | |{-- Outside Border
 *                    : :
 *    | | | | | | | | | | | | | | | | |
 *    | +-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |
 *    +-------------------------------+
 *                             (width, height)
 * </PRE>
 * <P>
 * The outside border doesn't get drawn (that's up to the application).
 * The Goban will leave enough space between the outside border and the
 * first grid line (both horizontal and vertical) so that a stone, when
 * placed on an intersection, will have its edge touch the outside
 * border (presuming, of course, the stone is placed on an intersection
 * at the edge of the Goban).
 * </P>
 * <P>
 * When dealing with the Goban, it is important to notice that points
 * of the intersections are ZERO-based. Thus the upper-left is at (0, 0),
 * the bottom right corner would be 18 x 18 for a 19-line board.
 * </P>
 * <P>
 * Applications can simply add this subclass of Panel anywhere they would
 * normally place a Panel. By using the API calls (such as placeStone,
 * removeMarkup, etc.) it's easy to change what's happening on the Goban.
 * </P>
 * <P>
 * Also note that the Goban knows nothing about the rules of Go. This
 * is the responsiblity of the Rules class, which monitors the Goban for
 * stone placements (i.e., an Observer). Since the Goban has no concept
 * of strings of stones, the Rules class must keep track of everything.
 * Even though this is a bit redundant, it allows for an enormous amount of
 * variations in what can be done with the Goban (e.g., a Rules class for
 * different games -- Go-moku, for instance).
 * </P>
 */
public class Goban extends Panel
{
	// Added by me, turn counter
	private static int turnCounter = 1;
	
	/** Most Gobans are 19 by 19. */
	public final static int DEFAULT_SIZE = 19;

	/** Default pixel size of the hoshi points. */
	public final static int DEFAULT_HOSHI_PIXELS = 6;

	/** Highlighting spot is not on the board/valid. */
	private final static int INVALID = -1;

	/** Black is used for drawing lines. */
	private final static Color DEFAULT_FGCOLOUR = Color.black;

	/** Lightgray is used for the board's background. */
	private final static Color DEFAULT_BGCOLOUR = Color.lightGray;

	/** Colour for highlighting intersections, if enabled. */
	private final static Color DEFAULT_HLCOLOUR = new Color( 0x99, 0xCC, 0xFF );

	private Stone[][] stones;
	private Markup[][] myMarks;

	private Color
		myFGColour = DEFAULT_FGCOLOUR,
		myBGColour = DEFAULT_BGCOLOUR,
		myHLColour = DEFAULT_HLCOLOUR;

	private Image myBGImage;

	private int myBoardSize = DEFAULT_SIZE;

	private boolean
		drawBG,								// Becomes true when image is set
		drawMarkup = true,		// Draw board markup by default
		drawHoshi = true,			// Draw star-points by default
		drawHighlight = true;	// Draw highlight by default

	private Stone mySizingStone;

	private Point myHighlight = new Point( INVALID, INVALID );

	/**
	 * The Goban delegates Observable-Observer information via the
	 * GobanObserver class.
	 */
	private GobanObserver myObserver = new GobanObserver();

	/** Used for double-buffering. */
	private Image myOffscreenImage;

	/**
	 * Used for double double-buffering. This image is the full Goban with
	 * stones, board markup, and hoshi points. It never has highlight. This
	 * is important for quickly repainting the board while highlighting
	 * the intersection over which the mouse hovers. Although redrawing
	 * the entire board is fast, the mouse can always move faster ... So
	 * this image prevents overflow of the repaint queue (which can crash the
	 * Java virtual machine).
	 */
	private Image myBasicBoard;

	/**
	 * By default, make the Goban update itself when stones are added/removed.
	 */
	private boolean forceRepaint = true;

	private int myHoshiPixelSize = DEFAULT_HOSHI_PIXELS;

	/**
	 * Creates a new instance of a Goban with default settings. A Goban must
	 * know the size of the stones that are going to be displayed on the board.
	 * Black and White stones must have the same dimensions (given in pixels).
	 * By changing the size of the sample stone, the size of the board will
	 * change the next time drawn. If a Goban is created without giving it a
	 * stone, it will create a default (white) stone to use for pixel sizing.
	 */
	public Goban() {
		this( new WhiteStone() );
	}

	/**
	 * Creates a new instance of a Goban at 19 lines (width by height).
	 *
	 * @param colour - The default background colour for the Goban.
	 */
	public Goban( Color colour ) {
		this( DEFAULT_SIZE, colour, new WhiteStone() );
	}

	/**
	 * Creates a new instance of a Goban at 19 lines (width by height).
	 *
	 * @param stone - The stone to use for setting the Goban's dimensions.
	 */
	public Goban( Stone stone ) {
		this( DEFAULT_SIZE, stone );
	}

	/**
	 * Creates a new instance of a Goban at 19 lines (width by height).
	 *
	 * @param colour - The default background colour for the Goban.
	 * @param stone - The stone to use for setting the Goban's dimensions.
	 */
	public Goban( Color colour, Stone stone ) {
		this( DEFAULT_SIZE, colour, stone );
	}

	/**
	 * Creates a new instance of a Goban at a given size, with defaults set.
	 *
	 * @param size - How many horizontal and vertical gridlines for this Goban.
	 * @param stone - The stone to use for setting the Goban's dimensions.
	 */
	public Goban( int size, Stone stone ) {
		this( size, DEFAULT_BGCOLOUR, stone );
	}

	/**
	 * Creates a new instance of a Goban at a given size, with a given colour,
	 * and remaining defaults set.
	 *
	 * @param size - How many horizontal and vertical gridlines for this Goban.
	 * @param colour - The background colour to use for this Goban.
	 * @param stone - The stone to use for setting the Goban's dimensions.
	 */
	public Goban( int size, Color colour, Stone stone ) {
		this( size, colour, null, stone );
	}

	/**
	 * Creates a new instance of a Goban at a given size, with a given
	 * background image, and remaining defaults set.
	 *
	 * @param size - How many horizontal and vertical gridlines for this Goban.
	 * @param bgImage - The background image (wood grain) to use for this Goban.
	 * @param stone - The stone to use for setting the Goban's dimensions.
	 */
	public Goban( int size, Image bgImage, Stone stone ) {
		this( size, DEFAULT_BGCOLOUR, bgImage, stone );
	}

	/**
	 * The big daddy of Goban constructors. This is used by all the other
	 * constructors, so if you want to avoid a slight overhead, use this
	 * one directly.
	 *
	 * @param size - How many horizontal and vertical gridlines for this Goban.
	 * @param colour - The background colour to use for this Goban.
	 * @param bgImage - The background image (wood grain) to use for this Goban.
	 * @param stone - The stone to use for setting the Goban's dimensions.
	 */
	public Goban( int size, Color colour, Image bgImage, Stone stone ) {
		setSizingStone( stone );
		setBGColour( colour );
		setBGImage( bgImage );

		setBoardSize( size );
		initializeBoard();
		calculateSize();
	}

	/**
	 * Creates a new instance of a Goban at a a given background image, and
	 * remaining defaults set.
	 *
	 * @param bgImage - The background image (wood grain) to use for this Goban.
	 * @param stone - The stone to use for setting the Goban's dimensions.
	 */
	public Goban( Image bgImage, Stone stone ) {
		this( DEFAULT_SIZE, DEFAULT_BGCOLOUR, bgImage, stone );
	}

	/**
	 * Determines the minimum pixel height and width required to display this
	 * Goban. The size is calculated according to the sizing stone dimensions.
	 *
	 * @return true - The size has changed; false otherwise.
	 */
	private void calculateSize()
	{
		int
			width = getSizingStone().getWidth() * getBoardSize() + 1,
			height = getSizingStone().getHeight() * getBoardSize() + 1;

		// Only create a new minimum (preferred) size if we have to.
		//
		if( (getWidth() != width) || (getHeight() != height) )
		{
			setBounds( 0, 0, width, height );
			setSize( new Dimension( width, height ) );
      setLocation( 0, 0 );

			// Force the next paint to draw everything.
			//
			setOffscreenImage( null );
		}
	}

  public Rectangle getBounds()
  {
    return new Rectangle( 0, 0, getSize().width, getSize().height );
  }

  public java.awt.Point getLocation() { return new java.awt.Point( 0, 0 ); }

	/**
	 * Resets the board to a pristine state: no stones, no marks.
	 */
	public void initializeBoard()
	{
		int size = getBoardSize();

		stones = new Stone[ size ][ size ];
		myMarks = new Markup[ size ][ size ];

		// If we are supposed to auto-refresh, then do so.
		//
		if( shouldForceRepaint() )
			forceRepaint();
	}

	public void update( Graphics g )
	{
		// If first time updating then create an offscreen buffer.
		//
		if( getOffscreenImage() == null ) {
			setOffscreenImage( createImage( getWidth(), getHeight() ) );
			redraw();
		}

		g.drawImage( getOffscreenImage(), 0, 0, null );
	}

	/**
	 * Called whenever damage occurs to the Goban's view. Simply calls
	 * update to repair things. This is the correct behaviour; many
	 * example applets have the main drawing code in paint: bad move.
	 */
	public void paint( Graphics g )
  {
    ((Graphics2D)g).setRenderingHint(
      RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

    update( g );
  }

	/**
	 * Called to ask the Goban to refresh its state. This method is
	 * is called, by default, whenever a stone is placed on the board.
	 */
	public void forceRepaint()
	{
		getGobanObserver().setChanged();
		redraw();
	}

	/**
	 * Used to draw the board on the offscreen graphics context. The board is
	 * drawn at (0, 0) and extends to its full width and height. This is the
	 * only place the entire contents of the board get painted to the offscreen
	 * buffer. Internally, there are two buffers. One to be used by the
	 * update method for bit-blasting, the other buffer stores an image of
	 * the entire board without highlighting.
	 * <P>
	 * Since highlighting must paint rather quickly, there isn't enough time
	 * to redraw everything all the time. Thus, a basic board image is drawn,
	 * stored, and highlighting can then be added to the basic image--provided
	 * nothing about the Goban has changed since the last time the mouse
	 * moved.
	 */
	private void redraw() {
		Image image = getOffscreenImage();

		// Not visible yet, so don't draw anything.
		//
		if( image == null )
			return;

		Graphics graphics = image.getGraphics();
		Image board = getBasicBoard();

		// If the board has changed, then we want to update the basic board to
		// reflect the current status of the board. This allows highlighting to
		// operate at the speed of mouse movements without causing the applet
		// to crash. (A bit-blasted image is orders of magnitude faster to draw
		// than trying to repaint the Goban's state from scratch every time.)
		//
		if( getGobanObserver().hasChanged() )
		{
			Graphics g = board.getGraphics();

			// 1. Draw the background image.
			//
			if( shouldDrawBG() )
				drawBG( g );
			else
			{
				g.setColor( getBGColour() );
				g.fillRect( 0, 0, getWidth(), getHeight() );
			}

			// 2. Draw the lines (having a toggle here would be a mind muck).
			//
			drawLines( g );

			// 3. Draw the hoshi points.
			//
			if( shouldDrawHoshi() )
				drawHoshi( g );

			// 4. Draw the stones.
			//
			drawStones( g );

			// 5. Draw the marks.
			//
			if( shouldDrawMarkup() )
				drawMarkup( g );

			getGobanObserver().clearChanged();
		}

		graphics.drawImage( board, 0, 0, null );

		// 6. Draw the highlight.
		//
		if( shouldDrawHighlight() ) {
			drawHighlight( graphics );
    }

		repaint();
	}

	/**
	 * Draws the board's background image, tiled.
	 *
	 * @param g - The graphics context on which the image is drawn.
	 */
	private void drawBG( Graphics g )
	{
		Image image = getBGImage();

    if( image == null )
      return;

		int
			bgWidth		= image.getWidth( null ),
			bgHeight	= image.getHeight( null ),
			width			= getWidth(),
			height		= getHeight();

		// Figure out the number of times we have to tile the image; add one
		// because chances are the tiling won't work out to an even number of
		// tiles ... so we have to do an extra row and column to fill the gap.
		//
		// Normally the width (resp. height) would be (width / bgWidth) + 1,
		// and since we're looping from the bottom-right to the upper-left which
		// would require -1, we find that the + 1 and - 1 cancel out. (This
		// is an added bonus to looping backwards, which is faster than looping
		// forwards since the compare operation to zero is faster than other
		// comparisons.) Unless there are many tiles, it's probably not worth
		// precalculating (x * bgWidth) into a variable.
		//
		for( int x = width / bgWidth; x >= 0; x-- )
			for( int y = height / bgHeight; y >= 0; y-- )
				g.drawImage( image, x * bgWidth, y * bgHeight, null );
	}

	/**
	 * Draws the board's grid.
	 *
	 * @param g - The graphics context on which the lines are drawn.
	 */
	private void drawLines( Graphics g )
	{
		int
			stoneWidth	= getSizingStone().getWidth(),
			stoneHeight = getSizingStone().getHeight(),
			halfWidth		= stoneWidth >> 1,
			halfHeight	= stoneHeight >> 1,
			x						= halfWidth,
			y						= halfHeight,
			width				= getBoardSize() * stoneWidth - halfWidth - 1,
			height			= getBoardSize() * stoneHeight - halfHeight - 1;

		g.setColor( getFGColour() );

		// Draw the horizontal and veritcal lines.
		//
		for( int lines = getBoardSize() - 1; lines >= 0; lines-- )
		{
			// Draw vertical lines.
			//
			g.drawLine( halfWidth, y, width, y );

			// Draw horizontal lines.
			//
			g.drawLine( x, halfHeight, x, height );

			x += stoneWidth;
			y += stoneHeight;
		}
	}

	/**
	 * Draws the board's stones. This method actually asks each of its stones
	 * to draw itself on the given graphics context at a particular point
	 * (given in pixels).
	 *
	 * @param g - The graphics context on which the stones are drawn.
	 */
	private void drawStones( Graphics g )
	{
		int
			size				= getBoardSize() - 1,
			stoneWidth	= getSizingStone().getWidth(),
			stoneHeight	= getSizingStone().getHeight();

    Stone[][] stones = getStones();

		for( int x = size; x >= 0; x-- )
		{
			// Unlike when drawing the tiled background image, here we can save
			// ourselves some speed by precalculating.
			//
			int xPixel = x * stoneWidth;

			for( int y = size; y >= 0; y-- )
			{
				Stone stone = stones[ x ][ y ];

				if( stone != null )
					stone.draw( g, xPixel, y * stoneHeight );
			}
		}
	}

  /**
   * Returns a handle to the array of stones.
   */
  private Stone[][] getStones()
  {
    return this.stones;
  }

	/**
	 * Draws the board's hoshi points.
	 *
	 * @param g - The graphics context on which the hoshi are drawn.
	 */
	private void drawHoshi( Graphics g )
	{
		int
			size				= getBoardSize(), 
			hoshi				= getHoshiPixelSize(),
			stoneWidth	= getSizingStone().getWidth(),
			stoneHeight	= getSizingStone().getHeight(),
			w2					= (stoneWidth >> 1) - (hoshi >> 1),
			h2					= (stoneHeight >> 1) - (hoshi >> 1);

		// No hoshi for Yoshi!
		//
		if( size < 9 )
			return;

		// Default hoshi positions.
		//
		int n1 = 3, n2 = 9, n3 = 15;

		// Even though these seem like "magic" numbers, they aren't. They are
		// the standard positions for hoshi points that have graced certain
		// intersections of Go boards for thousands of years, and most likely
		// are not going to change any time soon.
		//
		if( size < 13 )
		{
			n1 = 2; n2 = 4; n3 = 6;
		}
		else if( size < 19 )
		{
			n1 = 3; n2 = 6; n3 = 9;
		}

		int
			n1w = n1 * stoneWidth + w2,
			n2w = n2 * stoneWidth + w2,
			n3w = n3 * stoneWidth + w2;

		int
			n1h = n1 * stoneHeight + h2,
			n2h = n2 * stoneHeight + h2,
			n3h = n3 * stoneHeight + h2;

		g.setColor( getFGColour() );

		g.fillOval( n1w, n1h, hoshi, hoshi );
		g.fillOval( n1w, n2h, hoshi, hoshi );
		g.fillOval( n1w, n3h, hoshi, hoshi );

		g.fillOval( n2w, n1h, hoshi, hoshi );
		g.fillOval( n2w, n2h, hoshi, hoshi );
		g.fillOval( n2w, n3h, hoshi, hoshi );

		g.fillOval( n3w, n1h, hoshi, hoshi );
		g.fillOval( n3w, n2h, hoshi, hoshi );
		g.fillOval( n3w, n3h, hoshi, hoshi );
	}

	private int getHoshiPixelSize()
	{
		return myHoshiPixelSize;
	}

	/**
	 * Changes the number of pixels used for drawing hoshi points. 6 is
	 * generally a good number if the stones are 20 x 22, or so. If this
	 * must be altered, use an even number as odd numbers wind up drawing
	 * funky ovals (off-centre star points).
	 */
	public void setHoshiPixelSize( int pixels )
	{
		myHoshiPixelSize = pixels;
	}

	/**
	 * Draws the board's markup.
	 *
	 * @param g - The graphics context on which the markup is drawn.
	 */
	private void drawMarkup( Graphics g )
	{
		int
			size				= getBoardSize() - 1,
			stoneWidth	= getSizingStone().getWidth(),
			stoneHeight	= getSizingStone().getHeight();

		for( int x = size; x >= 0; x-- )
		{
			int xPixel = x * stoneWidth;

			for( int y = size; y >= 0; y-- )
			{
				Markup markup;

				if( (markup = myMarks[ x ][ y ]) != null )
				{
					int yPixel = (y * stoneHeight) - 1;

          Stone s = getStone( x, y );

					// If there's a stone, we don't want to clear.  At long last,
					// we no longer figure out what colour the markup should be ...
					// 'cause that sucked.  Just draw the markup and let someone
					// else figure out the correct colour.
					//
					if( s == null )
					{
						// Before we draw the markup, clear the lines by drawing
						// the background at the clip region which surrounds the
						// markup.
						//
						g.setClip( xPixel, yPixel + 1, stoneWidth, stoneHeight );
						drawBG( g );
						g.setClip( 0, 0, getWidth(), getHeight() );

            // Tell the markup that it is on an empty intersection (this
            // will change the colour accordingly).
            //
            markup.emptyIntersection();
					}

          if( s != null )
          {
            markup.stone( s.isWhite() );
          }

					markup.draw( g, xPixel, yPixel, stoneWidth, stoneHeight );
				}
			}
		}
	}

	/**
	 * If an intersection is supposed to be highlighted, this does it.  A
	 * subclass may override how highlighting is drawn.
	 */
	protected void drawHighlight( Graphics g )
	{
		Point p = getHighlight();

		if( isValid( p ) )
		{
			int
				stoneWidth	= getSizingStone().getWidth(),
				stoneHeight	= getSizingStone().getHeight(),
				x						= stoneWidth * p.x,
				y						= stoneHeight * p.y,
				w2					= stoneWidth >> 1,
				h2					= stoneHeight >> 1,
				adjX1				= 0,
				adjX2				= 0,
				adjY1				= 0,
				adjY2				= 0,
				size				= getBoardSize() - 1;

			g.setColor( getHighlightColour() );
			g.fillRect( x, y, stoneWidth, stoneHeight );

			// If we are at the edge of the board, then do not draw past,
			// in any direction (up, down, left, right).
			//
			if( p.x == 0 )
				adjX1 = w2;
			else if( p.x == size )
				adjX2 = w2;

			if( p.y == 0 )
				adjY1 = h2;
			else if( p.y == size )
				adjY2 = h2;

			g.setColor( getFGColour() );

			// Draw the horizontal line, then vertical.
			//
			g.drawLine( x + adjX1, y + h2, x + stoneWidth - adjX2, y + h2 );
			g.drawLine( x + w2, y + adjY1, x + w2, y + stoneHeight - adjY2 );
		}
	}

	private Color getHighlightColour() { return myHLColour; }

	/**
	 * Places a stone on the board, then notifies anyone who is observing that
	 * the given point has been updated with a new Stone. Either use
	 * "null" stone to remove a stone, or call the helper method "removeStone".
	 * The values for the Point are ZERO-based.  (0, 0) being the upper-left
	 * corner of the Goban, and (18, 18) being the bottom-right corner of
	 * a 19-line board.
	 * <P>
	 * Stones magically appear, but see setForceRepaint( boolean ) to turn this
	 * option off. This is useful when skipping to the end of the game.
	 *
	 * @param stone - The stone to place on the board; null means to remove.
	 * @param p - Where to place the stone
	 */
	public void placeStone( Stone stone, Point p )
  {
    Stone[][] stones = getStones();
		stones[ p.x ][ p.y ] = stone;

		// Let everyone know that a stone has been added. Nobody will know
		// if a stone has been removed, though.
		//
		/*if( stone != null ) {
			getGobanObserver().notifyObservers( p );
    	}

		// If we're in repaint mode (default), then update the board.
		//
		if( shouldForceRepaint() ) {
			forceRepaint();
    	}*/
	}

	/**
	 * Let's the world know if this Goban is automatically repainting the
	 * board when stones or markup are added/removed from it.  Default is
	 * true.
	 */
	public boolean shouldForceRepaint()
	{
		return this.forceRepaint;
	}

	/**
	 * Changes whether this Goban automatically repaints the board when stones
	 * are added or removed.  Default is true.
	 */
	public void setForceRepaint( boolean b )
	{
		this.forceRepaint = b;
	}

	/**
	 * Helper method. This simply calls "placeStone" with the same point
	 * and a null stone.
	 *
	 * @param p - Where to remove the stone
	 */
	public void removeStone( Point p )
	{
		placeStone( null, p );
  }

	/**
	 * Quickly removes all stones from the board. This is much faster than
	 * calling removeStone or placeStone.
	 */
	public void removeAllStones()
	{
		int size = getBoardSize();
    Stone[][] stones = getStones();

		for( int x = size - 1; x >= 0; x-- )
			for( int y = size - 1; y >= 0; y-- )
				stones[ x ][ y ] = null;

		if( shouldForceRepaint() )
			forceRepaint();
	}

	/**
	 * Lets the world know if the board has a stone at a given point.
	 *
	 * @param x - The x intersection coord. to examine for a stone.
	 * @param y - The y intersection coord. to examine for a stone.
	 */
	protected boolean hasStone( int x, int y )
	{
		return stones[ x ][ y ] != null;
	}

	/**
	 * Lets the world know if the board has a stone at a given point.
	 *
	 * @param p - The intersection to examine for a stone.
	 */
	public boolean hasStone( Point p )
	{
		return stones[ p.x ][ p.y ] != null;
	}

	protected boolean hasWhiteStone( int x, int y )
	{
		Stone stone = stones[ x ][ y ];
		return (stone == null) ? false : stone.isWhite();
	}

	/**
	 * Lets the world know if the board has a White stone at a given point.
	 * If no stone is located at the given point, this method returns false.
	 *
	 * @param p - The intersection to examine for a White stone.
	 * @return true - There is a white stone at the given point.
	 */
	public boolean hasWhiteStone( Point p )
	{
		return hasWhiteStone( p.x, p.y );
	}

	protected boolean hasBlackStone( int x, int y )
	{
		Stone stone = stones[ x ][ y ];
		return (stone == null) ? false : !stone.isWhite();
	}

	/**
	 * Lets the world know if the board has a Black stone at a given point.
	 * If no stone is located at the given point, this method returns false.
	 *
	 * @param p - The intersection to examine for a Black stone.
	 * @return true - There is a black stone at the given point.
	 */
	public boolean hasBlackStone( Point p )
	{
		return hasBlackStone( p.x, p.y );
	}

	/**
	 * Places a mark on the board. By default markup is drawn as soon as
	 * it is placed. If this behaviour is undesirable, then toggle it
	 * with the setForceRepaint( boolean ) method.  Notice that Markup
	 * is ZERO-based.  Like a stone, the upper-left is at (0, 0) and
	 * lower-right at (18, 18) for a 19-line board.
	 *
	 * @param markup - What type of markup to use at the given point
	 * @param x - x component where the mark should be placed
	 * @param y - y component where the mark should be placed
	 */
	public void placeMark( Markup markup, int x, int y )
	{
		myMarks[ x ][ y ] = markup;

		// If we're in repaint mode (default), then update the board.
		//
		if( shouldForceRepaint() )
			forceRepaint();
	}

	/**
	 * Helper method.  This simply calls "placeMark" with the (x, y) pair
	 * from the given point.
	 *
	 * @param markup - What type of markup to use at the given point
	 * @param p - Where to draw the mark
	 */
	public void placeMark( Markup markup, Point p )
	{
		placeMark( markup, p.x, p.y );
	}

	/**
	 * Helper method. This simply calls "placeMark" with the same (x, y)
	 * pair and null for the board mark.
	 *
	 * @param p - Where to remove the mark
	 */
	public void removeMark( Point p )
	{
		placeMark( null, p );
	}

	/**
	 * Quickly removes all marks from the board. This is faster than calling
	 * either placeMark or removeMark.
	 */
	public void removeAllMarks()
	{
		int size = getBoardSize();

		for( int x = size - 1; x >= 0; x-- )
			for( int y = size - 1; y >= 0; y-- )
				myMarks[ x ][ y ] = null;

		// A bit of code duplication, but since this is faster than calling
		// placeMark( ... ) all the time, we'll have to live with it.
		//
		if( shouldForceRepaint() )
			forceRepaint();
	}

	/**
	 * Overrides the default behaviour for addObserver in order to ensure that
	 * the observer may be added at most once. Observers are told when a stone
	 * has been placed on the board. They are not told when a stone is removed.
	 * The second argument to the corresponding "update" method is the point
	 * at which the stone was added.
	 * <P>
	 * Notice that this delegates the request to our internal GobanObserver
	 * instance.
	 */
	public synchronized void addObserver( Observer o )
	{
		getGobanObserver().addObserver( o );
	}

	/**
	 * Answers whether the given (x, y) coordinates are valid for this Goban.
	 *
	 * @return true - The given coordinates can be found on this Goban.
	 */
	public boolean isValid( int x, int y )
	{
		// If the x values are valid and the y values are valid, then the
		// coordinates are valid.
		//
		return
			((x >= 0) && (x < getBoardSize())) &&
			((y >= 0) && (y < getBoardSize()));
	}

	/**
	 * Answers whether the given point is valid for this Goban. This is
	 * a helper method.
	 *
	 * @return true - The given point can be found on this Goban.
	 */
	public boolean isValid( Point p )
	{
		return isValid( p.x, p.y );
	}

	protected Stone getStone( int x, int y )
	{
		return stones[ x ][ y ];
	}

	/**
	 * Classes within this package may get a handle to the stone at the given
	 * point. But since this is kind of dangerous (as setting its width
	 * and height is a bad thing), only classes within the package will have
	 * this ability.  This will return null if no stone exists at the given
	 * point.
	 *
	 * @param p - Where to look for a stone on the Goban.
	 * @return The stone at the point, or null.
	 * @throws ArrayIndexOutOfBoundsException
	 */
	protected Stone getStone( Point p )
	{
    return getStone( p.x, p.y );
	}

	/**
	 * Some virtual machines like to tell us what size we are; we don't like
	 * that very much, so thumb to the nose!	Even though this is technically
	 * deprecated, there are older browsers that aren't aware of the future,
	 * just yet.
	 *
	 * @deprecated
	 */
	public void reshape( int x, int y, int width, int height )
	{
		super.reshape( x, y, width, height );

		// Nobody, but nobody, reshapes us.
		//
		calculateSize();
	}

	/**
	 * Specifies the preferred area (in pixels) that the Goban needs in order
	 * to display a full grid such that all the stones will be visible.
	 */
	public Dimension getPreferredSize() { return getSize(); }

	/**
	 * See getPreferredSize.
	 */
	public Dimension getMinimumSize() { return getPreferredSize(); }

	/**
	 * We have a maximum size, yes ...
	 */
	public Dimension getMaximumSize() { return getPreferredSize(); }

	/**
	 * Helper method; returns the Goban's width in pixels.
	 */
	public int getWidth() { return getBounds().width; }

	/**
	 * Helper method; returns the Goban's height in pixels.
	 */
	public int getHeight() { return getBounds().height; }

	/**
	 * Changes the number of lines for this Goban.
	 *
	 * @param size - The number of lines displayed by this Goban.
	 */
	private void setBoardSize( int size ) { myBoardSize = size; }

	/**
	 * Returns the number which represents both the number of rows and columns
	 * for this Goban's grid.
	 *
	 * @return The number of lines for this Goban.
	 */
	public int getBoardSize() { return myBoardSize; }

	/**
	 * @return The background image (wood grain) used when drawing the Goban
	 * (the image is tiled).
	 */
	private Image getBGImage() { return myBGImage; }

	/**
	 * Changes the background image used when drawing the Goban.  If the
	 * image is passed in as null, then the background won't be drawn.
	 *
	 * @param image - The new background image.
	 */
	public void setBGImage( Image image )
	{
		setDrawBG( image != null );

		myBGImage = image;
	}

	/**
	 * Calculates the point that the pixel coordinates map to for this
	 * Goban. Even though it is bad form to pass in the point, it
	 * saves from having to create a new instance Point each time. Since
	 * "translateCoord" can be called quite often, this is a necessity. If
	 * this method returns true, then the given coordinates were successfully
	 * mapped to their equivalent intersection on the Goban; the result is
	 * stored in Point's (x, y) pair.
	 *
	 * @param mX - The x value (in pixels) of the intersetion to retrieve.
	 * @param mY - The y value (in pixels) of the intersetion to retrieve.
	 * @param p - Where to put the resulting intersection.
	 * @return true - The given Point instance has valid values.
	 */
	// !!TODO constants need to be replaced this is totally hard coded
	public boolean translateCoord( int mX, int mY, Point p )
	{
		mX = mX - 32;  // 32 is X_POSITION
		mY = mY - 32;
		
		if( (mX < 0) || (mX >= (36*19) - 1) ||       
				(mY < 0) || (mY >= (36*19) - 1) ) {		// 36 is TILE_SIZE, 20 is BOARD_SIZE + 1
			return false;
		}

		// mouseX and mouseY are now guaranteed to be somewhere on the board;
		// calculate the position.
		
		p.x = mX / 36;						// 36 is TILE_SIZE
		p.y = mY / 36;
		
		return true;
	}
	

	private void setOffscreenImage( Image image ) {
    myOffscreenImage = image;
  }

	private Image getOffscreenImage() { return myOffscreenImage; }

	/**
	 * Returns the stone used to calculate the width and height of the Goban.
	 *
	 * @return The stone used to calculate the width and height of the Goban
	 * in pixels.
	 */
	public Stone getSizingStone() { return mySizingStone; }

	/**
	 * Changes the stone used to calculate the width and height of the Goban
	 * in pixels.
	 *
	 * @param stone - The new sizing stone.
	 */
	private void setSizingStone( Stone stone ) { mySizingStone = stone; }

	/**
	 * Changes the foreground colour used in drawing hoshi and board lines.
	 *
	 * @param colour - The new foreground colour.
	 */
	public void setFGColour( Color colour )
	{
		myFGColour = colour;
		forceRepaint();
	}

	/**
	 * @return The foreground colour used in drawing hoshi and board lines.
	 */
	private Color getFGColour() { return myFGColour; }

	/**
	 * Changes the background colour used for the board.
	 *
	 * @param colour - The new background colour.
	 */
	public void setBGColour( Color colour )
	{
		myBGColour = colour;
		forceRepaint();
	}

	/**
	 * @return The background colour used for the board.
	 */
	private Color getBGColour() { return myBGColour; }

	protected Point getHighlight() { return myHighlight; }

	/**
	 * Answers whether the given point is the same spot as the Goban's
	 * currently highlighted point.
	 *
	 * @param p - The Goban p to check for a highlight.
	 * @return true - The points are the same.
	 */
	public boolean isHighlighted( Point p )
	{
		return getHighlight().equals( p );
	}

	/**
	 * Checks to see if highlighting on.
	 *
	 * @return true - Highlighting is being added to this Goban.
	 */
	public boolean isHighlighted()
	{
		return (myHighlight.x != INVALID) && shouldDrawHighlight();
	}

	/**
	 * Asks the Goban to highlight a particular intersection.
	 *
	 * @param x - The column component of the intersection.
	 * @param y - The row component of the intersection.
	 */
	public void setHighlight( int x, int y )
	{
		if( isValid( x, y ) )
		{
			myHighlight.x = x;
			myHighlight.y = y;
		}
		else
			myHighlight.x = INVALID;

		redraw();
	}

	/**
	 * Helper method. Simply calls "setHighlight( x, y )" with the values
	 * stored in the Point parameter.
	 *
	 * @param p - Where the Goban should highlight an intersection.
	 */
	public void setHighlight( Point p )
	{
		setHighlight( p.x, p.y );
	}

	/**
	 * Asks the Goban to clear its highlighted intersection. Nothing happens
	 * if the highlighting was already cleared.
	 */
	public void clearHighlight()
	{
		setHighlight( INVALID, INVALID );
	}

	/**
	 * Answers whether this Goban is drawing a background image (typically
	 * a wood grain).
	 *
	 * @return true - The background image should be drawn.
	 * @return false - The background image should not be drawn.
	 */
	public boolean shouldDrawBG() { return drawBG; }

	/**
	 * Changes whether the background image is drawn. The default is to draw.
	 *
	 * @param b - Set true if the background image should be drawn.
	 */
	private void setDrawBG( boolean b )
	{
		drawBG = b;
		forceRepaint();
	}

	/**
	 * Answers whether this Goban is drawing hoshi points.
	 *
	 * @return true - The hoshi (star points) should be drawn.
	 * @return false - The hoshi (star points) should not be drawn.
	 */
	public boolean shouldDrawHoshi() { return drawHoshi; }

	/**
	 * Asks the Goban to draw its hoshi points, if it isn't,
	 * or to stop if it is.
	 */
	public void toggleHoshi() { setDrawHoshi( !shouldDrawHoshi() ); }

	/**
	 * Changes whether the hoshi points are drawn. The default is to draw.
	 *
	 * @param b - Set true if the hoshi points should be drawn.
	 */
	private void setDrawHoshi( boolean b )
	{
		drawHoshi = b;
		forceRepaint();
	}

	/**
	 * Answers whether this Goban is drawing board markup.
	 *
	 * @return true - The board markup should be drawn.
	 * @return false - The boark markup should not be drawn.
	 */
	public boolean shouldDrawMarkup() { return drawMarkup; }

	/**
	 * Asks the Goban to draw board markup if it isn't, otherwise stop.
	 * Markup is drawn by default.
	 */
	public void toggleMarkup() { setDrawMarkup( !drawMarkup ); }

	/**
	 * Changes whether the markup is drawn. The default is to draw.
	 *
	 * @param b - Set true if the markup should be drawn.
	 */
	private void setDrawMarkup( boolean b ) {
		drawMarkup = b;
		forceRepaint();
	}

	private final Image getBasicBoard() {
		if( myBasicBoard == null ) {
			myBasicBoard = createImage( getWidth(), getHeight() );
    }

		return myBasicBoard;
	}

	/**
	 * Answers whether this Goban is drawing highlighted intersections.
	 *
	 * @return true - The highlighting should be drawn.
	 * @return false - The highlighting should not be drawn.
	 */
	public boolean shouldDrawHighlight() { return drawHighlight; }

	/**
	 * Asks the Goban to begin drawing highlight if it isn't, otherwise stop.
	 * Highlighting is drawn by default. Although useless without having
	 * instantiated a GobanHighlighter ...
	 */
	public void toggleHighlight() { setDrawHighlight( !shouldDrawHighlight() ); }

	/**
	 * Changes whether the highlight is drawn. The default is to draw.
	 * Although useless without having instantiated a GobanHighlighter ...
	 *
	 * @param b - Set true if the highlight should be drawn.
	 */
	private void setDrawHighlight( boolean b )
	{
		drawHighlight = b;
		forceRepaint();
	}

	/**
	 * Creates a copy of this Goban's graphics image.
	 *
	 * @param g - The graphics context on which to draw the Goban.
	 */
	public void copyGraphics( Graphics g ) {
		g.drawImage( getOffscreenImage(), 0, 0, null );
	}
	
	
	/**
	 * added by me: increments the turn
	 */
	public void incrementTurn() {
        //captureCheck();
        turnCounter++;
    }
	
	/**
	 * added by me: returns the current turn number
	 */
	public int getTurnCounter() {	
		return turnCounter;
	}

	private GobanObserver getGobanObserver() { return myObserver; }

	/**
	 * Used to inform observers of changes to a Goban. Ideally, the Goban
	 * should be observable, but since the Goban <B>must</B> be a Panel
	 * (in order to implement a complicated off-screen double double-buffering
	 * technique), delegation is an alternative to allow Goban observers.
	 * <P>
	 * Inner classes require a JDK v1.1 compiler; they are v1.0.2 compatible.
	 */
	public class GobanObserver extends Observable
	{
		protected GobanObserver()
		{
			setChanged();
		}

		/**
		 * Adds an Observer to this Observable subclass. Doesn't allow multiple
		 * observers on the same Goban. This method is public not by choice;
		 * ideally it should be protected; however since the constructor is
		 * protected, this shouldn't cause any glaring encapsulation holes.
		 *
		 * @param o - The observer to add
		 */
		public void addObserver( Observer o )
		{
			deleteObserver( o );
			super.addObserver( o );
		}

		/**
		 * Indicates this Goban has changed in some way.
		 */
		protected void setChanged()
		{
			super.setChanged();
		}

		/**
		 * Indicates this Goban has no more changes.
		 */
		protected void clearChanged()
		{
			super.clearChanged();
		}

		/**
		 * Used to tell all Observers that the given Point on the Goban has
		 * changed. At the moment, this only happens when a stone is added.
		 *
		 * @param p - The point on the Goban that has changed.
		 */
		protected void notifyObservers( Point p )
		{
			setChanged();
			super.notifyObservers( p );
		}
	}
}
