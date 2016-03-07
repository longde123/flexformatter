package ds.hindu.core.business
{
	default xml namespace=what

	public interface IHinduClubsDelegate
	{
		function getClubBalance(clubId:String, resultHandler:Function):void
		function getClubs(resultHandler:Function):void
		function transferMoneyToClubAccount(password:String, clubId:String, amount:String, resultHandler:Function, faultHandler:Function):void
	}
}
