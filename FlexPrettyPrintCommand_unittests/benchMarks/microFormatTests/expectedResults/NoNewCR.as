function s()
{
	if (x) return true;
	if (x) break;
	if (x) continue;
	if (x) throw new Error("message");
	if (x) callFunc(a, b, 10, subFunc(6000, "abcde"));
}