package be.as3.flex.charting.axis
{
import mx.charts.chartClasses.DateRangeUtilities;
import mx.core.mx_internal;
import mx.charts.AxisLabel;

use namespace mx_internal

/**
* Flex 4 - DateTimeAxis label bug correction.
*
* @see http://forums.adobe.com/message/2913753
*/
public class DateTimeAxis extends mx.charts.DateTimeAxis
{
public function DateTimeAxis()
{
super();
}

override protected function buildLabelCache():Boolean
{
if (super.buildLabelCache())
{
var r:Number = computedMaximum - computedMinimum - new DateRangeUtilities().calculateDisabledRange(computedMinimum, computedMaximum);
for each (var label:AxisLabel in labelCache)
{
label.position = ((label.value as Date).time - computedMinimum) / r;
}
return true;
}
else
{
return false;
}
}
}
}
