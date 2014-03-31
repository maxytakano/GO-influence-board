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
import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 * A board markup item.
 */
public class Markup
{
	/** The mark type is unknown. */
	public final static int UNKNOWN	= 0;

	/** The mark is a circle. */
	public final static int CIRCLE = 1;

	/** The mark is a square. */
	public final static int SQUARE = 2;

	/** The mark is a triangle. */
	public final static int TRIANGLE = 3;

	/** The mark is a number. */
	public final static int NUMBER = 4;

	/** The mark is a letter. */
	public final static int LETTER = 5;

	private int myType = UNKNOWN;

	private int myNumber;
	private char myLetter;

	private Color myColour = Color.black;

	/**
	 * Creates a new board markup.
	 *
	 * @param markupType - The type of markup this object represents.
	 */
	public Markup( int markupType )
	{
		setType( markupType );
	}

	/**
	 * Creates a new board markup, with a specific colour.
	 *
	 * @param markupType - The type of markup this object represents.
	 * @param colour - The colour to use when drawing the markup.
	 */
	public Markup( int markupType, Color colour )
	{
		setType( markupType );
		setColour( colour );
	}

	/**
	 * Used to draw the mark at a given location on the graphics context.
	 * Notice that the mark does not centre itself about this point; the
	 * Goban is responsible for making sure the marks are centred about a point.
	 *
	 * @param g - The graphics context on which to draw.
	 * @param x - The x location to begin drawing.
	 * @param y - The y location to begin drawing.
	 * @param width - Number of pixels wide for this markup.
	 * @param height - Number of pixels high for this markup.
	 */
	protected void draw( Graphics g, int x, int y, int width, int height )
	{
		// Since the X and Y are the coordinates of the centre of the stone,
		// we have to adjust the markup because otherwise the centre of the
		// stone would be the top left of the markup.  This means the x and y
		// values must be shifted a titch in order to centre the markup about
		// the given intersection pixel values.
		//
		int
			w = width >>= 1,
			h = height >>= 1;

		// Centre the mark about an intersection, with temporary x and y values.
		// Adjust for unevenness. Odd widths/heights need an extra pixel, so if
		// the lowest significant bit is set, add 1 to the x/y values.
		//
		int
			tx = x + (w >> 1) + (w & 1),
			ty = y + (h >> 1) + (h & 1);

		g.setColor( getColour() );

		// Figure out our type of Markup, then draw it on the graphics context.
		// If the type isn't here, there's not much we can do.
		//
		switch( getType() )
		{
			case CIRCLE:
				drawCircle( g, tx, ty, w, h ); break;
			case SQUARE:
				drawSquare( g, tx, ty, w, h ); break;

			// The triangle has to shift up a bit to look correct.  Of course
			// this will have to change to take into account stone size ...
			//
			case TRIANGLE:
				drawTriangle( g, tx, ty - 2, w, h );
				break;

			// Numbers and letters need special attention as the Font will
			// have a width and descent pixels that must be considered.
			//
			case NUMBER:
				drawNumber( g, x + width, y + height ); break;
			case LETTER:
				drawLetter( g, x + width, y + height ); break;
		}
	}

	/**
	 * Draws: O
	 */
	private void drawCircle( Graphics g, int x, int y, int width, int height )
	{
		g.drawOval( x, y, width, height );
	}

	/**
	 * Draws: []
	 */
	private void drawSquare( Graphics g, int x, int y, int width, int height )
	{
		g.drawRect( x, y, width, height );
	}

	/**
	 * Draws: /_\
	 */
	private void drawTriangle( Graphics g, int x, int y, int width, int height )
	{
		// Draw: /
		//
		g.drawLine( x + (width / 2), y, x, y + height );

		// Draw: \
		//
		g.drawLine( x + (width / 2), y, x + width, y + height );

		// Draw: _
		//
		g.drawLine( x, y + height, x + width, y + height	);
	}

	/**
	 * Draws: a b c d e f g h i j k l m n o p q r s t u v w x y or z (upper
	 * case also may be drawn)
	 */
	private void drawLetter( Graphics g, int x, int y )
	{
		drawString( g, "" + getLetter(), x, y );
	}

	/**
	 * Draws: 0 1 2 3 4 5 6 7 8 or 9
	 */
	private void drawNumber( Graphics g, int x, int y )
	{
		drawString( g, Integer.toString( getNumber() ), x, y );
	}

	private void drawString( Graphics g, String s, int x, int y )
	{
		// Get the FontMetrics for this Graphics context to make sure
		// the string is centred about the given point.
		//
		FontMetrics fm = g.getFontMetrics();
		g.drawString( s, x - (fm.stringWidth( s ) >> 1), y + fm.getDescent() );
	}

	/**
	 * Changes the (foreground) colour used for drawing the mark.
	 *
	 * @param colour - The new foreground colour.
	 */
	public void setColour( Color colour )
	{
		myColour = colour;
	}

	private Color getColour() { return myColour; }

	protected int getType() { return myType; }

	/**
	 * Changes this board markup's type.
	 */
	public void setType( int type ) { myType = type; }

	protected int getNumber() { return myNumber; }

	/**
	 * Changes this board markup's number. This only has an affect if the
	 * markup type is NUMBER.
	 */
	public void setNumber( int number ) { myNumber = number; }

	protected char getLetter() { return myLetter; }

	/**
	 * Changes this board markup's text letter. This only has an affect if the
	 * markup type is LETTER.
	 */
	public void setLetter( char letter ) { myLetter = letter; }

	public String toString()
	{
		return getType() == LETTER ? "Letter" : "Circle";
	}

  /**
   * Changes the default colour of the markup. This is called by the goban
   * when drawing markup on non-stone locations.
   */
  protected void emptyIntersection()
  {
    setColour( Color.black );
  }

  /**
   * Changes the colour to draw depending on the underlying stone colour.
   * Black stones get white markup and white stones get black markup.
   *
   * @param white - The colour of stone on the Goban.
   */
  protected void stone( boolean white )
  {
    setColour( white ? Color.black : Color.white );
  }
}

