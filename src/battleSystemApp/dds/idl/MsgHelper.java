package battleSystemApp.dds.idl;


/**
* battleSystemApp/dds/idl/MsgHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from TacticalSymbolsData.idl
* mi�rcoles 7 de mayo de 2014 13H41' CEST
*/

abstract public class MsgHelper
{
  private static String  _id = "IDL:battleSystemApp/dds/idl/Msg:1.0";

  public static void insert (org.omg.CORBA.Any a, battleSystemApp.dds.idl.Msg that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static battleSystemApp.dds.idl.Msg extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  private static boolean __active = false;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      synchronized (org.omg.CORBA.TypeCode.class)
      {
        if (__typeCode == null)
        {
          if (__active)
          {
            return org.omg.CORBA.ORB.init().create_recursive_tc ( _id );
          }
          __active = true;
          org.omg.CORBA.StructMember[] _members0 = new org.omg.CORBA.StructMember [4];
          org.omg.CORBA.TypeCode _tcOf_members0 = null;
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _members0[0] = new org.omg.CORBA.StructMember (
            "unitID",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_double);
          _members0[1] = new org.omg.CORBA.StructMember (
            "lat",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_double);
          _members0[2] = new org.omg.CORBA.StructMember (
            "lon",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_double);
          _members0[3] = new org.omg.CORBA.StructMember (
            "alt",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_struct_tc (battleSystemApp.dds.idl.MsgHelper.id (), "Msg", _members0);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static battleSystemApp.dds.idl.Msg read (org.omg.CORBA.portable.InputStream istream)
  {
    battleSystemApp.dds.idl.Msg value = new battleSystemApp.dds.idl.Msg ();
    value.unitID = istream.read_string ();
    value.lat = istream.read_double ();
    value.lon = istream.read_double ();
    value.alt = istream.read_double ();
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, battleSystemApp.dds.idl.Msg value)
  {
    ostream.write_string (value.unitID);
    ostream.write_double (value.lat);
    ostream.write_double (value.lon);
    ostream.write_double (value.alt);
  }

}