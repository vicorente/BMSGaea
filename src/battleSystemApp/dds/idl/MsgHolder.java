package battleSystemApp.dds.idl;

/**
* battleSystemApp/dds/idl/MsgHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from TacticalSymbolsData.idl
* mi�rcoles 7 de mayo de 2014 13H41' CEST
*/

public final class MsgHolder implements org.omg.CORBA.portable.Streamable
{
  public battleSystemApp.dds.idl.Msg value = null;

  public MsgHolder ()
  {
  }

  public MsgHolder (battleSystemApp.dds.idl.Msg initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = battleSystemApp.dds.idl.MsgHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    battleSystemApp.dds.idl.MsgHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return battleSystemApp.dds.idl.MsgHelper.type ();
  }

}
