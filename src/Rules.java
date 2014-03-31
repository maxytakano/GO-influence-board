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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * Online at: http://www.gnu.org/copyleft/gpl.html
 */

//package com.joot.jigo;

/**
 * The rules that govern the behaviour of a particular Goban. The Super Ko
 * rule is not present, however it is entirely possible to create a subclass
 * which implements the rule.
 * <P>
 * By observing the Rules class, you can be notified when the number of
 * captures for either black or white has changed.
 * <P>
 * Example usage:
 *
 * <PRE>
 * Stone stone = new WhiteStone( STONE_WIDTH, STONE_HEIGHT );
 * Point point = new Point();
 * Goban goban = new Goban();
 * Rules rules = new Rules( goban );
 * 
 * if( goban.translateCoord( mouseX, mouseY, point ) )
 *   if( rules.canPlay( stone.isWhite(), point ) )
 *     goban.placeStone( stone, point );
 * </PRE>
 */
public class Rules
{
  /** An unexamined position inside the stone map. */
  private final static byte UNEXAMINED = 0;

  /** Indiciates a liberty for a group of stones in the stone map. */
  private final static byte LIBERTY    = 1;

  /** Indiciates a stone is present inside the stone map. */
  private final static byte STONE      = 2;

  /**
   * A map for stones and liberties. Used for the recursive liberty count
   * method. Also used for removing a group of stones.  This is accessed
   * directly in a number of positions to increase speed; be careful!
   */
  private byte
    emptyStoneMap[],
    stoneMap[][];

  private Goban goban;

  /** Force only legal Ko play; do not permit self capture. */
  private boolean
    _legalKo = true,
    _legalSelfCapture,
    whiteToPlay;

  /**
   * The position of the ko capture.
    */
  private Point ko = new Point( -1, -1 );

  /**
   * Default komi is 0.0 points for white.
   */
  private double komi;

  /**
   * The rules of Go dictate how to count the score at the end of the game.
   * Thus, even though it isn't entirely intuitive, the application must be
   * able to query the number of black and white captures to this point.
   * A double is used because white is normally given a 0.5 point komi.
   */
  private double
    whiteCaptures,
    blackCaptures;

  /**
   * Creates a new instance of rules for a game of Go, given the Goban to
   * which the rules apply.
   */
  public Rules( Goban goban )
  {
    setGoban( goban );

    // The stone map must be created so we can count liberties. Rather
    // than having it initialized each time we count, while we examine the
    // array we reset its values to UNEXAMINED as we progress.
    //
    initStoneMap( goban.getBoardSize() );
  }

  /**
   * Called when the given Goban has had a stone placed on the board.
   * The point is also given. The first parameter is ignored, as the
   * Goban this Rule set is watching was given at instantiation. Probably
   * not a good idea to call this method directly.  It's only public because
   * of the Observer/Observable API.
   *
   * @param goban - The Goban on which the given stone was played.
   * @param stonePoint - The point at which the stone was played.
   */
  //public void update( Observable goban, Object stonePoint )
  public void update( Goban goban, Point stonePoint )
  {
    // Examine the stone on the goban at the given point.
    //
    Point point = (Point)stonePoint;
    Stone placed = getGoban().getStone( point );
    int captures = 0;
    
    // Toggle white's turn.
    //TODO what is this doing
    setWhiteToPlay( !placed.isWhite() );
    
    // Create a point to keep track of the KO spot
    Point koPoint = new Point();
    boolean captureFlag = false;

    // If the number of liberties for an orthagonal group of opposite colour
    // to the stone played is zero, then we remove the group at that point.
    //
    // Search Up. Y = -1 (relative to original position)
    //
    point.y--;
    captures += tryCullGroup( placed, point );
    
    // Set Ko Location
    if (captures == 1 && captureFlag == false) {
    	koPoint.x = point.x;
    	koPoint.y = point.y;
    	captureFlag = true;
    }

    // Search Down. Y = +1 (relative to original position)
    //
    point.y += 2;
    captures += tryCullGroup( placed, point );

    // Set Ko Location
    if (captures == 1 && captureFlag == false) {
    	koPoint.x = point.x;
    	koPoint.y = point.y;
    	captureFlag = true;
    }

    // Search Left.
    //   Y = 0 (relative to original position)
    //   X = -1 (relative to original position)
    //
    point.y--;
    point.x--;
    captures += tryCullGroup( placed, point );
    
    // Set Ko Location
    if (captures == 1 && captureFlag == false) {
    	koPoint.x = point.x;
    	koPoint.y = point.y;
    	captureFlag = true;
    }

    // Search Right. X = +1 (relative to original position)
    //
    point.x += 2;
    captures += tryCullGroup( placed, point );
    
    // Set Ko Location
    if (captures == 1 && captureFlag == false) {
    	koPoint.x = point.x;
    	koPoint.y = point.y;
    	captureFlag = true;
    }

    // Put the X value back to where it belongs, just in case other code
    // uses the X & Y values in "stonePoint" (which is quite likely).
    //
    point.x--;

    // If we captured a single prisoner, set this spot to indicate that
    // a ko is taking place.  Otherwise, indicate that no ko is happening.
    //    
    setKo( (captures == 1) ? koPoint : null );
  }

  /**
   * Returns true if legal Ko plays are enabled.  By default this returns
   * true.
   *
   * @return true - Only legal Ko captures may be made.
   */
  public boolean isLegalKoEnabled() { return _legalKo; }

  /**
   * Toggles the state of legal Ko captures.
   */
  public void toggleLegalKo() { _legalKo = !_legalKo; }

  /**
   * Returns true if self captures are allowed.  By default this returns
   * false.
   *
   * @return true - Self captures are allowed.
   */
  public boolean isSelfCaptureEnabled() { return _legalSelfCapture; }

  /**
   * Toggles whether self captures are allowed.
   */
  public void toggleSelfCapture() { _legalSelfCapture = !_legalSelfCapture; }
  
  /**
   * Returns the number of points komi used in this game. At the moment,
   * black cannot be given a komi (reverse komi). This feature might not
   * be implemented, as SGF files don't take reverse komi into consideration.
   * Typical komi for modern games varies between 5.5 and 8.5. The default
   * value is 0.5.
   *
   * @return The komi white gets for black's advantage in playing first.
   */
  public double getKomi() { return this.komi; }
  
  /**
   * Allows the system to change the amount of komi given to white.
   */
  public void setKomi( double komi ) { this.komi = komi; }
  
  /**
   * Returns the number of captured white stones (Black prisoners).
   */
  public double getWhiteCaptures() { return this.whiteCaptures; }

  /**
   * Returns the number of captured black stones (White prisoners).
   */
  public double getBlackCaptures() { return this.blackCaptures; }

  /**
   * Since this class is continually watching the game, it knows the
   * colour of the player who made the previous move.  Following from
   * that it can also determine if a play at a specific spot is legal
   * without having to know the colour of the stone that wants to be
   * set upon the Goban.
   *
   * @param p - Somewhere on the Goban a move wants to be played.
    */
  public boolean canPlay( Point p )
  {
    return canPlay( isWhiteToPlay(), p );
  }

  /**
   * Returns a true or false, depending on whether the given stone can be
   * legally played at the given point.
   *
   * @param white - Is it white's turn to place a stone?
   * @param p - The intersection to check for play legality.
   * @return true - The point is a valid intersection to play the stone.
   */
  public boolean canPlay( boolean white, Point p )
  {
    // Simple check first: if the point is taken, then the stone may not
    // be played.
    // 
    if( getGoban().hasStone( p ) )
      return false;

    // Check against the Ko rule, make sure the move is not on the ko spot.
    // This takes precidence over self-capture. If the spot is where the
    // ko was played (if the previous move was a ko), then it's illegal.
    //
    if( isLegalKoEnabled() && isKo( p ) )
      return false;

    // If there are no liberties adjacent to the spot, then check to see
    // if any groups about the spot (in opposite colour) have one liberty.
    // If a group that is adjacent to the spot we want to check has only
    // one liberty, then the spot we are checking must be its last liberty.
    // This would imply that we can play there because something would get
    // captured.  On the other hand, if self capture is on, then we can
    // always play.
    //
    if( countLiberties( p.x, p.y ) == 0 )
    {
      // If there is an adjacent group of the same colour that has some
      // liberties, then it is a legal move regardless of capturing.
      //
      if( libertiesAfterPlaying( white, p ) > 0 )
        return true;

      // If you don't capture anything, and there are no liberties at the
      // given point, then you may not play there.  Unless, of course,
      // self-capture is enabled.  If the played stone would join up some
      // stones, then that is an okay spot to play, too.
      //
      if( wouldCapture( white, p.x, p.y ) )
        return true;

      // At this point the stone to be placed doesn't join up to get more
      // liberties, nor does it capture anything.  If self-capture is on,
      // then return true because it captures itself.
      //
      return isSelfCaptureEnabled();
    }

    // A stone can be played at the given point without breaking the
    // rules, so we return true.
    //
    return true;
  }

  /**
   * Sets the captures back to initial values (typically 0.5 and 0.0 for
   * white captures and black captures, respectively).
   */  
  public void resetCaptures()
  {
    //resetWhiteCaptures();
    //resetBlackCaptures();
  }

  /**
   * Sets the number of white captures back to the value of komi.
   */
  //public void resetWhiteCaptures() { setWhiteCaptures( getKomi() ); }
  
  /**
   * Sets the number of black captures back to zero.
   */
  //public void resetBlackCaptures() { setBlackCaptures( 0 ); }

  /**
   * Whenever we add someone as an observer, immediately let them know our
   * status.
   */
  /*public void addObserver( Observer o )
  {
    super.addObserver( o );
    setChanged();
    notifyObservers();
  }*/

  /**
   * Changes whose turn it is to make a move on the board.
   */
  public void togglePlayer()
  {
    setWhiteToPlay( !isWhiteToPlay() );
  }

  /**
   * Returns true if it is white's turn to place a stone on the board.
   */
  public boolean isWhiteToPlay()
  {
    return this.whiteToPlay;
  }

  /**
   * Doesn't actually nuke the group without checking first. If there
   * happens to be a stone at the given point, then the number of liberties
   * for the group of stones which contains the stone at that point is
   * counted. If the number of liberties is zero, then the group at the
   * given point is removed.
   * <P>
   * For the record, this proves Yoda wrong. "There is no try, there is either
   * do or do not."  We try. ;-)
   *
   * @param placed - The stone that was just placed on the board.
   * @param toCheck - An intersection orthagonal to the stone that was placed. 
   * @return The number of stones removed from the Goban.
   */
  private int tryCullGroup( Stone placed, Point toCheck )
  {
    int dead = 0;

    if( !isValid( toCheck.x, toCheck.y ) )
      return dead;

    Stone compare = getGoban().getStone( toCheck );

    // If the stones are opposite in colour and the liberties at the given
    // point (orthagonal to the "placed" stone) have all been squelched,
    // then the group may be safely removed from the board.
    //
    if( (compare != null) && (placed.isWhite() != compare.isWhite()) )
    {
      resetStoneMap();

      // Once we count the liberties, we have a map of the stones. If
      // the number of liberties is zippo, then use that same stone map
      // to remove the stones from the goban.
      //
      if( countGroupLiberties( toCheck.x, toCheck.y ) == 0 )
        dead = removeStones();

      // Cart-Master: "Bring out your dead! Bring out your dead!"
      // Man: "Here's one."
      // Cart-Master: "Ninepence."
      // Old Man: "I'm not dead!"
      // Cart-Master: "What?"
      // Man: "Nothing. Here's your ninepence ..."
      // Old Man: "I'm not dead!"
      // Cart-Master: "Ere! 'E says 'e's not dead!"
      // Man: "Yes he is."
      // Old Man: "I'm not!"
      // Cart-Master: "'E isn't?"
      // Man: "Well ... he will be soon--he's very ill."
      // Old Man: "I'm getting better!"
      // Man: "No you're not, you'll be stone dead in a moment."
      // Cart-Master: "I can't take 'im like that! It's against regulations!"
      // Old Man: "I don't want to go on the cart ..."
      // Man: "Stop being such a baby."
      // Cart-Master: "I can't take 'im ..."
      // Old Man: "I feel fine!"
      // Man: "Well, do us a favour ..."
      // Cart-Master: "I can't!"
      // Man: "Can you hang around a couple of minutes?  He won't be long."
      // Cart-Master: "No, gotta' get to Robinson's, they lost nine today."
      // Man: "Well, when's your next round?"
      // Cart-Master: "Thursday."
      // Old Man: "I think I'll go for a walk."
      // Man (to Old Man): "You're not fooling anyone, you know."
      // Man (to Cart-Master): "Look, isn't there something you can do?"
      // Old Man: "I feel happy! I feel happy!"
      // Cart-Master gives the Old Man a blow to the head with a wooden
      // spoon; the Old Man goes limp.
      // Man throws Old Man into cart: "Ah, thanks very much."
      // Cart-Master: "Not at all. See you on Thursday!"
      //
      
      /*if( dead > 0 )
      {
        if( compare.isWhite() )
          setWhiteCaptures( getWhiteCaptures() + dead );
        else
          setBlackCaptures( getBlackCaptures() + dead );
      }*/
    }

    return dead;
  }

  /**
   * Removes all the stones marked in the stone map.
   * <P>
   * This method relies on the fact that a stone map has already been
   * created (by calling makeStoneMap). Since a group must be removed if
   * its liberties reach zero, and there's no easy way to tell if a group has
   * no more liberties outside of actually counting them (via a stone map),
   * this method indirectly relies on the stone map created by a call to
   * countGroupLiberties().
   * </p>
   *
   * @return The number of stones removed.
   */
  private int removeStones()
  {
    byte map[][] = getStoneMap();
    Goban goban = getGoban();

    int
      length = map.length - 1,
      dead = 0;

    Point p = new Point();

    for( byte x = (byte)length; x >= 0; x-- )
    {
      for( byte y = (byte)length; y >= 0; y-- )
      {
        if( map[ x ][ y ] == STONE )
        {
          p.x = x;
          p.y = y;
          goban.removeStone( p );
          dead++;
        }
      }
    }

    // "The fact that slaughter is a horrifying spectacle must make us take
    // war more seriously, but not provide an excuse for gradually blunting
    // our swords in the name of humanity. Sooner or later someone will come
    // along with a sharp sword and hack off our arms."
    //     -- Carl von Clausewitz
    //
    return dead;
  }

  /**
   * Returns the number of liberties for a group after placing a stone
   * at a given point.
   */
  private int libertiesAfterPlaying( boolean white, Point p )
  {
    byte map[][] = getStoneMap();

    resetStoneMap();

    makeStoneMap( white, p.x, p.y - 1, map );
    makeStoneMap( white, p.x, p.y + 1, map );
    makeStoneMap( white, p.x - 1, p.y, map );
    makeStoneMap( white, p.x + 1, p.y, map );

    // Return all the liberties found, except the one for the stone that'll
    // get played, which chews a liberty from the total.
    //
    return countStoneMap( LIBERTY ) - 1;
  }

  /**
   * Returns true if the first point and second point are in the same group
   * of stones.
   *
   * @return true - The needle Point is contained by the group that contains
   * the haystack Point.
   */
  private boolean sameGroup( Point needle, Point haystack )
  {
    boolean white = getGoban().hasWhiteStone( haystack );

    return
      sameGroup( needle.x - 1, needle.y, haystack ) ||
      sameGroup( needle.x + 1, needle.y, haystack ) ||
      sameGroup( needle.x, needle.y - 1, haystack ) ||
      sameGroup( needle.x, needle.y + 1, haystack );
  }

  private boolean sameGroup( int x, int y, Point haystack )
  {
    return true;
  }

  /**
   * Returns true if playing a stone at the given location would capture
   * a group of stones.
   *
   * @param isWhite - Set to true if white is to play the stone at the point.
   * @param p - The point to play a stone.
   */
  private boolean wouldCapture( boolean isWhite, int x, int y )
  {
    // Look up, down, left, then right.
    //
    return
      captures( isWhite, x, y - 1 ) ||
      captures( isWhite, x, y + 1 ) ||
      captures( isWhite, x - 1, y ) ||
      captures( isWhite, x + 1, y );
  }

  /**
   * Returns true if playing a stone at the given point would capture some
   * stones.
   */
  private boolean captures( boolean white, int x, int y )
  {
    // Only look at stones opposite in colour to determine if any
    // would be captured.
    //
    if( isValid( x, y ) && (getGoban().hasBlackStone( x, y ) == white) )
    {
      resetStoneMap();

      if( countGroupLiberties( x, y ) == 1 )
        return true;
    }

    return false;
  }

  private int liberties( boolean white, int x, int y )
  {
    // Only look at stones opposite in colour to determine if any
    // would be captured.
    //
    if( isValid( x, y ) && (getGoban().hasWhiteStone( x, y ) == white) )
    {
      resetStoneMap();
      return countGroupLiberties( x, y );
    }

    return 0;
  }

  /**
   * Returns true if the given point is the latest ko point.
   *
   * @param point - The place where the stone is about to be played.
   * @return true - The given point matches the Japanese ko point.
   */
  private boolean isKo( Point p )
  {
    return getKo().equals( p );
  }

  /**
   * Counts the liberties around a given point.  Of course, this is usually
   * under the presumption that there exists a stone at the given point whose
   * liberties are to be counted.  It doesn't bother to make sure there is
   * in fact a stone at the given point.  Do not confuse this with counting
   * the number of liberties in a group of stones -- a much more difficult
   * problem.  This is used to help check for a Ko stone.
   * <P>
   * Tehcnically the algorithm could be sped up by returning as soon as
   * we find more than 1 liberty.  But this is a bit more elegant from a
   * design standpoint -- the method can be reused as it stands.
   *
   * @param p - The point whose liberties are to be counted.
   * @return The number of free spaces surrounding the given point, or zero.
   */
  private int countLiberties( int x, int y )
  {
    return
      countLiberty( x, y - 1) +
      countLiberty( x, y + 1 ) +
      countLiberty( x - 1, y ) +
      countLiberty( x + 1, y );
  }

  /**
   * @return 1 - There is one liberty (no stone).
   * @return 0 - There are no liberties (a stone exists).
   */
  private int countLiberty( int x, int y )
  {
    // If it's a valid spot, then see if there's a stone in order to figure
    // out if we should return 1 (counted liberty) or 0.  If it is not valid,
    // then it doesn't count as a liberty -- no matter what!  If there is
    // a stone at the spot then it does not count as a liberty.
    //
    return isValid( x, y ) ? (hasLiberty( x, y ) ? 1 : 0) : 0;
  }

  /**
   * Returns true if the Goban does not have a stone at the given coordinates.
   */
  private boolean hasLiberty( int x, int y )
  {
    return !getGoban().hasStone( x, y );
  }

  /**
   * Counts the number of liberties for a group of stones at a given point.
   * The group can be a group of one stone. Returns 0 if there are no
   * stones at the point. In a real game of Go, 0 liberties means something
   * has died, thus can never represent the liberty count for a group of
   * stones still present on the Goban. Since this Rules class is meant
   * to govern a game of Go, this presumption is kosher.
   * <P>
   * This method creates a stone mapping for the group of stones at the
   * given point. The stone mapping is valid after a call to this method.
   * Before calling this method a second time, be sure to clear the
   * stone map manually. This is because the map created by this method
   * is used by the removeStones method for efficiency's sake (no sense
   * mapping the same group of stones twice in a row).
   *
   * @return The number of liberties for the group of stones at the given
   * point.
   */
  private int countGroupLiberties( int x, int y )
  {
    Stone stone = getGoban().getStone( x, y );

    // Of course, we can't count liberties for stones that aren't present.
    // (Well, we could as the algorithm yields one [1], but this isn't
    // correct behaviour, so ...)
    //
    if( stone == null )
      return 0;

    // Map the stones and their liberties for the group of stones at the
    // given point. After we make the map, we do not clear it!  This
    // is critical because removeStones relies on the map made from a call
    // to this method. So even though "countStoneMap" could reset the
    // map as it goes, we don't.
    //
    makeStoneMap( stone.isWhite(), x, y, getStoneMap() );

    // After the mapping of the group is complete, iterate over the stone
    // map to tally its liberties. The stone map remains intact after calling
    // this method so that an attempt at removing dead stones can be made.
    //
    return countStoneMap( LIBERTY );
  }

  /**
   * This sets up the empty stone map such that every spot on the Goban maps
   * to an empty space (unexamined).  When the stone map is reset, it does
   * a straightforward copy of the empty stone map, which is a little over
   * four times faster (according to my test of many iterations at 19x19).
   */
  private void initStoneMap( int size )
  {
    // Used for resetting the stone map when the time comes.
    //
    this.emptyStoneMap = new byte[ size ];

    // We need some memory set up for the work-horse stone map, otherwise
    // we'd be resetting the stone map into no-memory-land. Normally this
    // would use an accessor, but this is the only place the stone map is
    // ever allocated. It is reset and reused elsewhere, but never changed
    // in size.
    //
    this.stoneMap = new byte[ size ][ size ];
  }

  /**
   * Creates a new map for counting liberties of groups. Since counting
   * liberties is performed recursively, a map of is required to track
   * liberties (and stones) that have already been counted for a group.
   * This actually performs a System.arraycopy on a preinitialised array
   * that is filled up with the LIBERTY entity.
   */
  private void resetStoneMap()
  {
    // Values are initialized to 0, by default.  We could speed this up
    // by precreating an array and doing a fast System.arrayCopy( ... ).
    // However, we'd have to track if the Goban size changed.
    //
    byte
      emptyStoneMap[] = this.emptyStoneMap,
      stoneMap[][] = getStoneMap();

    int size = emptyStoneMap.length;

    for( int i = size - 1; i >= 0; i-- )
      System.arraycopy( emptyStoneMap, 0, stoneMap[i], 0, size );
  }
  
  /**
   * Returns true if the given (x, y) pair are valid coordinates for the
   * Goban associated with these rules. This delegates the question to the
   * Goban.
   *
   * @return true - The given coordinates are on the Goban.
   */
  private boolean isValid( int x, int y )
  {
    return getGoban().isValid( x, y );
  }

  /**
   * Returns true if the given Point has a valid pair of coordinates for the
   * Goban associated with these rules.
   */
  private boolean isValid( Point p )
  {
    return getGoban().isValid( p );
  }

  /**
   * Helper method that relies on the fact that the Goban allows every class
   * in this package to get a handle on a Stone at a given point.  If no
   * stone is at the given location, then this cannot create a map and will
   * return false.  If the given coordinates are not valid, this will throw
   * an ArrayIndexOutOfBounds exception.
   *
   * @return true - The stone map was created.
   * @throw ArrayIndexOutOfBoundsException - The coordinates are beyond the
   * board's boundaries.
   */
  private boolean makeStoneMap( int x, int y )
  {
    Stone stone = getGoban().getStone( x, y );

    if( stone == null )
      return false;

    resetStoneMap();

    // We know for certain that there is a stone at the given location,
    // so we can return true to indicate that the stone map was created.
    //
    makeStoneMap( stone.isWhite(), x, y, getStoneMap() );
    return true;
  }

  /**
   * Recursive method used to count the liberties of a group of stones
   * at a given point. The stoneColour must not be null. The x and y
   * values can be invalid -- e.g, (-42, 367). At the moment, only
   * countGroupLiberties calls this method (removeStones() relies on this
   * method being called via countGroupLiberties).
   * <P>
   * Also indicates the points of all the stones belonging to the
   * group of the stone at the given coordinates.
   *
   * @param white - Indicates if white stones should be counted.
   * @param goban - Passed in to avoid calling getGoban() a few times.
   */
  private void makeStoneMap( boolean white, int x, int y, byte map[][] )
  {
    // Don't go knocking where stones aren't rocking.
    //
    if( !isValid( x, y )  )
      return;

    // If a stone has been mapped already, we go no further. This is the
    // main recursion stopper condition (MRSC).
    //
    if( map[ x ][ y ] != UNEXAMINED )
      return;

    Stone stone = getGoban().getStone( x, y );

    // No stone means we've found a liberty.
    //
    if( stone != null )
    {
      // Make sure the colours are compatible before recursing. This must
      // be checked AFTER it is known that a stone is at the given coordinates
      // since "hasWhiteStone" will return false if there is no stone, as well
      // as if ther is a black stone at the given point.
      //
      if( stone.isWhite() == white )
      {
        // We've found a stone, mark the stone map so it isn't re-recursed.
        // This is used in concert with the MRSC (above).
        //
        map[ x ][ y ] = STONE;

        // Don't have to worry about going over the board's boundaries, since
        // we immediately check for valid values during recursion.
        //
        makeStoneMap( white, x, y - 1, map );
        makeStoneMap( white, x, y + 1, map );
        makeStoneMap( white, x - 1, y, map );
        makeStoneMap( white, x + 1, y, map );
      }
    }
    else
      map[ x ][ y ] = LIBERTY;
  }

  /**
   * Counts the number of a specific kind of entities that exist within
   * a stone map.  This is used to count liberties or stones.  It could
   * be used to count the number of unexamined spots on the board, but
   * that information is rather useless as an unexamined spot could be
   * a stone, liberty, or empty due to the fact that the stone map is used
   * to count only one type of entity at a time (and typically for a
   * specific region of the Goban).
   *
   * @param entity - Either LIBERTY or STONE.
   */
  private int countStoneMap( int entity )
  {
    // Always faster to reference a local variable, than class-scope.
    //
    byte map[][] = getStoneMap();

    int
      total = 0,
      length = map.length - 1;

    // Tally the total number of entities.
    //
    for( int x = length; x >= 0; x-- )
      for( int y = length; y >= 0; y-- )
        total += (map[ x ][ y ] == entity) ? 1 : 0;

    return total;
  }

  /**
   * The GobanHighlighter needs to be told which Rules to used for asking
   * about legal moves.  The only way it can ask the Rules for legal moves
   * is by observing mouse movements on the Goban.  To simplify the API
   * this method is made protected (from private) so that the highlighter
   * need only be told which Rules to use to see if a particular colour
   * may play at a specific location -- and thus ask the Goban to highlight
   * that intersection.
   */
  protected Goban getGoban() { return this.goban; }

  /**
   * Changes the Goban that this rule set watches for new stone placements.
   */
  private void setGoban( Goban goban )
  {
    if( goban != null )
    {
      this.goban = goban;
      //this.goban.addObserver( this );
    }
  }

  /**
   * Changes the number of white captures to the number given. Notifies
   * observers.
   *
   * @captures - The number of stones black has captured.
   */
  /*private void setWhiteCaptures( double captures )
  {
    this.whiteCaptures = captures;
    setChanged();
    notifyObservers();
  }*/

  /**
   * Changes the number of white captures to the number given. Notifies
   * observers.
   *
   * @captures - The number of stones black has captured.
   */
  /*private void setBlackCaptures( double captures )
  {
    this.blackCaptures = captures;
    setChanged();
    notifyObservers();
  }*/

  /**
   * Sets the most recent Ko point location.
   */
  private void setKo( Point p )
  {
    if( p != null )
    {
      this.ko.x = p.x;
      this.ko.y = p.y;
    }
    else
      this.ko.x = -1;
  }

  /**
   * Returns the latest values for the Ko point.
   */
  private Point getKo()
  {
    return this.ko;
  }

  /**
   * Set to true if it is white's turn to place a stone on the board.
   *
   * @param play - true means white gets to make the next move.
   */
  private void setWhiteToPlay( boolean play )
  {
    this.whiteToPlay = play;
  }

  /**
   * Returns the internal map used to gain context over the goban.
   */
  private byte[][] getStoneMap()
  {
    return this.stoneMap;
  }
}

