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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

/**
 * A Black stone that can be placed on a Goban.
 * <P>
 * The Goban will resize itself according to the size of a White stone.
 */
public class BlackStone extends Stone
{
	/**
	 * Create a new Black stone with a default width and height.
	 */
	public BlackStone() { }

	/**
	 * Create a new Black stone with a specific width and height.
	 *
	 * @param width -- The stone's width (in pixels).
	 * @param height -- The stone's height (in pixels).
	 */
	public BlackStone( int width, int height )
	{
		super( width, height );
	}

	/**
	 * Create a new Black stone with a specific image.
	 *
	 * @param stoneImage -- The image to use when drawing this stone.
	 */
	public BlackStone( Image stoneImage )
	{
		super( stoneImage );
	}

	/**
	 * Returns false, since this is a black stone.
	 *
	 * @return false - This is a Black stone.
	 */
	public boolean isWhite() { return false; }
}

