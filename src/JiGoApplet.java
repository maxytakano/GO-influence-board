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

import java.applet.Applet;

import java.awt.Image;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * Most applets using the JiGo API will want to extend this class. The
 * responsibilities for this class include loading up the basic images
 * (white stone, black stone, board background) and settings (background
 * colour, default stone width/height, and such).
 *
 * <P>
 * Subclasses need only override the "start" and "stop" methods (the latter
 * being optional).
 */
public class JiGoApplet extends Applet
{
  private static final String PARAM_STONE_WIDTH   = "STONE WIDTH";
  private static final String PARAM_STONE_HEIGHT  = "STONE HEIGHT";
  private static final String PARAM_BOARD_SIZE    = "BOARD SIZE";
  private static final String PARAM_WHITE_STONE   = "WHITE STONE";
  private static final String PARAM_BLACK_STONE   = "BLACK STONE";
  private static final String PARAM_BOARD_IMAGE   = "BOARD IMAGE";
  private static final String PARAM_APPLET_COLOUR = "APPLET COLOUR";

  private static final String PARAM_EMBED         = "EMBED";

	/** The applet's default background colour. */
	private static final String DEFAULT_BG = "#FFFFFF";

	/** Used for reading images from a URL. */

	private BlackStone myBlackStone = new BlackStone();
	private WhiteStone myWhiteStone = new WhiteStone();
	private Image myBGImage;

	private int myBoardSize;

	/**
	 * Empty, public, constructor so that subclasses can exist.
	 */
	public JiGoApplet() { }

	/**
	 * Called by the browser to initialize the applet. This loads up all
	 * images and relevant settings.
	 */
	public void init()
	{
		int
			stoneWidth = getParam( PARAM_STONE_WIDTH, Stone.DEFAULT_WIDTH ),
			stoneHeight = getParam( PARAM_STONE_HEIGHT, Stone.DEFAULT_HEIGHT );

		setBoardSize( getParam( PARAM_BOARD_SIZE, Goban.DEFAULT_SIZE ) );

	}



	/**
	 * Helper method for determining the integer value of a parameter. If the
	 * specified parameter doesn't exist (or isn't a number), then the given
	 * default value is returned. Otherwise, the integer value of the given
	 * parameter is returned.
	 *
	 * @param paramName - A parameter from the applet's PARAM tag.
	 * @param defaultValue - Returned if the parameter was invalid.
	 */
	protected int getParam( String paramName, int defaultValue )
	{
		try
		{
			return Integer.parseInt( getParameter( paramName ) );
		}
		catch( Exception e ) { }

		return defaultValue;
	}

	/**
	 * Helper method for determining the String value of a parameter. If the
	 * specified parameter doesn't exist, then the given default value is
	 * returned; otherwise, the String value of the given parameter comes back.
	 *
	 * @param paramName - A parameter from the applet's PARAM tag.
	 * @param defaultValue - Returned if the parameter was invalid.
	 */
	protected String getParam( String paramName, String defaultValue )
	{
		try
		{
			return getParameter( paramName );
		}
		catch( Exception e ) { }

		return defaultValue;
	}

  /**
   * Returns true if the given parameters indicate a value of 'TRUE'.
   */
  protected boolean getBooleanParam( String param, boolean defaultValue )
  {
    // Suppress failures by default.
    //
    Boolean result = new Boolean( defaultValue );

    try
    {
      result = new Boolean( getParameter( param ) );
    }
    catch( Exception e )
    {
      // No need to do anything.
      //
    }

    return result.booleanValue();
  }

  /**
   * Returns true if the applet should be embedded into the web page where
   * its applet tag resides. By default, this returns false to indicate that
   * the applet should create an independent frame.
   */
  protected boolean embed()
  {
    return getBooleanParam( PARAM_EMBED, false );
  }

	/**
	 * Converts a filename into a fully qualified URL. The URL is based on
	 * this applet's code base, since the applet may only communicate with
	 * the server from whence it came (thus document base would foil the code).
	 * The fileName is specified by relative path to where the applet's class
	 * files can be found.
	 *
	 * @param fileName - The name of the file to convert into a URL.
	 */
	private URL file2url( String fileName )
	{
		try
		{
			return new URL( getCodeBase() + fileName );
		}
		catch( Exception e ) { }

		return null;
	}

	/**
	 * The number of gridlines for the Goban that will be created.
	 *
	 * @return Usually an integer in the set { 9, 13, 19 }.
	 */
	protected int getBoardSize() { return myBoardSize; }
	private void setBoardSize( int size ) { myBoardSize = size; }

	/**
	 * The White stone used by the Goban when displaying its stones.
	 *
	 * @return An instance of WhiteStone.
	 */
	public WhiteStone getWhiteStone() { return myWhiteStone; }
	private void setWhiteStone( WhiteStone ws )
	{
		myWhiteStone = ws;
	}

	/**
	 * The Black stone used by the Goban when displaying its stones.
	 *
	 * @return An instance of BlackStone.
	 */
	public BlackStone getBlackStone() { return myBlackStone; }
	private void setBlackStone( BlackStone bs )
	{
		myBlackStone = bs;
	}

	/**
	 * The background image used by the Goban for its wood grain. This is
	 * either a GIF JPEG image, which should be tilable. This method can
	 * return null if no image is specified.
	 *
	 * @return An image to be drawn for a Goban's background.
	 */
	protected Image getBGImage() { return myBGImage; }
	private void setBGImage( Image i ) { myBGImage = i; }
}

