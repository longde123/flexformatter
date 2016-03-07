package x
{

	/**
	 * Class comment.  This class
	 * doesn't really do anything important.
	 */
	class AClass
	{
		var i:int; /* a comment that is on one line but would be wrapped otherwise*/
		var i:int; /** a doc comment that is on one line but that would be wrapped otherwise*/

		/* A comment describing a variable where
			the comment is very long
			or at least part of it is
			but could be collapsed. */
		var j:int=2;

		/* another
			line
			comment
		 */
		i++;

		//A line comment that describes a variable
		//and it's unclear what else could happen
		var g:Object;

		var m:Object; //A comment that would be wrapped if it were on a line by itself.
	}
}
