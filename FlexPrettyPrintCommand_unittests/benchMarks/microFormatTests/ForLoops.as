package x
{
	class y
	{
		function z()
		{
			for (;;);
			for (;;)
			for (var i:int=2;i<10;++i);
         for (var i:Number=2,j:int=4;j<10 && i<10;i++,j++);
			for (i++;i<10;i++);
			for (i+=1;i<10;i++);
			for (i+j;i<10;i++);
			for (callFunc();i<10;i++);
			var items:Array = ["hi", "there", "bob"];
for (var index in items) {
	trace(index+" => "+items[index]); // traces "0 => hi", "1 => there" and "2 => bob"
}
var items:Array = ["hi", "there", "bob"];
for each (var value:String in items) {
	trace(value); // traces "hi", "there" and "bob"
}
var numbers:Array = [2, 3, 5, 7];
for each (var prime:uint in numbers) {
	trace(prime); // traces 2, 3, 5 and 7
}
var books:Vector.<XML> = new Vector.<XML>();
books.push(<book><author>John Smith</author><title>The Book About Nothing</title></book>);
books.push(<book><author>Jane Doe</author><title>The Book About Everything</title></book>);
for each (var book:XML in books) {
	trace(book.author); // will again correctly trace authors
}
		}
	}
}