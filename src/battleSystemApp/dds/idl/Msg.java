package battleSystemApp.dds.idl;


/**
* battleSystemApp/dds/idl/Msg.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from TacticalSymbolsData.idl
* mi�rcoles 7 de mayo de 2014 13H41' CEST
*/

/**
* Updated by idl2j
* from TacticalSymbolsData.idl
* mi�rcoles 7 de mayo de 2014 13H41' CEST
*/

import org.opensplice.mobile.dcps.keys.KeyList;

@KeyList(
    topicType = "Msg",
    keys = {"unitID"}
)
public final class Msg implements org.omg.CORBA.portable.IDLEntity
{
  public String unitID = null;
  public double lat = (double)0;
  public double lon = (double)0;
  public double alt = (double)0;

  public Msg ()
  {
  } // ctor

  public Msg (String _unitID, double _lat, double _lon, double _alt)
  {
    unitID = _unitID;
    lat = _lat;
    lon = _lon;
    alt = _alt;
  } // ctor

} // class Msg