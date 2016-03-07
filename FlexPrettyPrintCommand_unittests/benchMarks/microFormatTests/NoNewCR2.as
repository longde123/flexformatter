function s()
{
	if (x) if (y) z++;
	for (var i:int=2;i<10;i++)     trace("false");
	while (true) return null;
	if (x) for (;;);
	if (x) return true;
	if (x) var x:Boolean;
	if (x) {}
	if (x) break;
	if (x) continue;
	if (x) throw new Error("message");
	if (x) callFunc(a, b, 10, subFunc(6000, "abcde"));
	if (x) y++; else y--;
}