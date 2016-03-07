function s(arg1:Object,arg2:LongClassName,arg3:Blah,... rest):void
{
	callFunc(arg1, arg3 + arg4, longvariableName, anotherLongVariableName);
	callFunc(arg1, arg3+arg4, longvariableName, blah, anotherLongVariableName);
	performSelect(new MyType(), getBooleanValueFromSomewhereElse(
			true || aVeryLongVariableName), true, getBooleanValueFromSomewhereElse(true,
			false), false, anotherVeryLongFunc(arg1, arg2, arg3, arg4), true);
	performSelect(new MyType(), getBooleanValueFromSomewhereElse(true,
			true || aVeryLongVariableName), true, getBooleanValueFromSomewhereElse(true,
			false), false, anotherVeryLongFunc(arg1, arg2, arg3, arg4), true);
	
}