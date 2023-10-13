/*
 * Copyright (C) 2012 Luca Veltri - University of Parma - Italy
 * 
 * This file is part of MjSip (http://www.mjsip.org)
 * 
 * MjSip is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * MjSip is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with MjSip; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Author(s):
 * Luca Veltri (luca.veltri@unipr.it)
 */

package org.mjsip.sip.provider;



import org.mjsip.sip.message.SipMethods;
import org.slf4j.LoggerFactory;
import org.zoolu.util.Configure;
import org.zoolu.util.Parser;
import org.zoolu.util.Timer;



/** SIP configuration options. 
  * <p>
  * Options are: the default SIP port, deafult supported transport protocols,
  * timeouts, log configuration, etc.
  * </p>
  */
public class SipConfig extends Configure {
	
	private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(SipConfig.class);

	/** Whether SipStack configuration has been already loaded */
	private static boolean is_init=false;

	/** Default SIP port.
	 * Note that this is not the port used by the running stack, but simply the standard default SIP port.
	 * <br> Normally it sould be set to 5060 as defined by RFC 3261. Using a different value may cause
	 * some problems when interacting with other unaware SIP UAs. */
	public static int default_port=5060; 
	/** Default SIP port for TLS transport (SIPS).
	 * Note that this is not the port used by the running stack, but simply the standard default SIPS port.
	 * <br> Normally it sould be set to 5061 as defined by RFC 3261. Using a different value may cause
	 * some problems when interacting with other unaware SIP UAs. */
	public static int default_tls_port=5061; 
	/** Default supported transport protocols. */
	public static String[] default_transport_protocols={ SipProvider.PROTO_UDP, SipProvider.PROTO_TCP };
	/** Default max number of contemporary open transport connections. */
	public static int default_nmax_connections=32;
	/** Whether adding 'rport' parameter on via header fields of outgoing requests. */
	public static boolean use_rport=true;
	/** Whether adding (forcing) 'rport' parameter on via header fields of incoming requests. */
	public static boolean force_rport=false;

	/** For TLS. Whether all client and server certificates should be considered trusted. */
	public static boolean default_tls_trust_all=false;
	/** For TLS. names of the files containing trusted certificates.
	  * The file names include the full path starting from the current working folder. */
	public static String[] default_tls_trusted_certs=null;
	/** For TLS. Path of the folder where trusted certificates are placed.
	  * All certificates (with file extension ".crt") found in this folder are considered trusted. */
	public static String default_tls_trust_folder="cert";
	/** For TLS. Absolute file name of the certificate (containing the public key) of the local node.
	  * The file name includes the full path starting from the current working folder. */
	public static String default_tls_cert_file="cert/ssl.crt";
	/** For TLS. Absolute file name of the private key of the local node.
	  * The file name includes the full path starting from the current working folder. */
	public static String default_tls_key_file="cert/ssl.key";


	// ********************* transaction timeouts *********************

	/** starting retransmission timeout (milliseconds); called T1 in RFC2361; they suggest T1=500ms */
	public static long retransmission_timeout=500;  
	/** maximum retransmission timeout (milliseconds); called T2 in RFC2361; they suggest T2=4sec */
	public static long max_retransmission_timeout=4000;   
	/** transaction timeout (milliseconds); RFC2361 suggests 64*T1=32000ms */
	public static long transaction_timeout=32000;    
	/** clearing timeout (milliseconds); T4 in RFC2361; they suggest T4=5sec */
	public static long clearing_timeout=5000;

	// ******************** general configurations ********************

	/** default max-forwards value (RFC3261 recommends value 70) */
	public static int max_forwards=70;
	/** Whether the default timer mode is 'daemon', or not.
	 * In 'daemon' mode, when all other threads terminate, the program also ends
	 * regardless the timer was still running, and no timeout callback is fired.
	 * In 'non-daemon' mode, the program ends only when all active timers have expired
	 * or explicitly halted. */
	public static boolean timer_daemon_mode=true;
	/** Whether at UAS side automatically sending (by default) a 100 Trying on INVITE. */
	public static boolean auto_trying=true;
	/** Whether 1xx responses create an "early dialog" for methods that create dialog. */
	public static boolean early_dialog=true;
	/** Whether automatically sending PRACK messsages for incoming reliable 1xx responses in an INVITE dialog.
	  * <br> Note that if you set <i>true</i>, the PRACK messge are sent automatically without any message body.
	  * This may be in contrast with a possible offer/answer use of reliable 1xx response and PRACK. */
	public static boolean auto_prack=false;
	/** Default 'expires' value in seconds. RFC2361 suggests 3600s as default value. */
	public static int default_expires=3600;
	/** UA info included in request messages in the 'User-Agent' header field.
	  * Use "NONE" if the 'User-Agent' header filed must not be added. */
	public static String ua_info=SipStack.release;
	/** Server info included in response messages in the 'Server' header field
	  * Use "NONE" if the 'Server' header filed must not be added. */
	public static String server_info=SipStack.release; 
	/** Supported option-tags for corresponding supported extensions. */
	public static String[] supported_option_tags={ SipStack.OTAG_100rel,SipStack.OTAG_timer }; //{ OTAG_100rel,OTAG_timer,OTAG_precondition };
	/** Required option-tags for corresponding required extensions. */
	public static String[] required_option_tags=null; //{ OTAG_100rel,OTAG_timer };
	/** List of supported methods. */
	public static String[] allowed_methods={ SipMethods.INVITE,SipMethods.ACK,SipMethods.OPTIONS,SipMethods.BYE,SipMethods.CANCEL,SipMethods.INFO,SipMethods.PRACK,SipMethods.NOTIFY,SipMethods.MESSAGE,SipMethods.UPDATE };
	/** Minimum session interval (Min-SE header field) for supporting "Session Timers" (RFC 4028). */
	public static int min_session_interval=90;
	/** Default session interval (Session-Expires header field) for supporting "Session Timers" (RFC 4028). */
	public static int default_session_interval=0;


	// ************** registration client configurations **************

	/** starting registration timeout (msecs) after a registration failure due to request timeout */
	public static long regc_min_attempt_timeout=60*1000; // 1min
	/** maximum registration timeout (msecs) after a registration failure due to request timeout */
	public static long regc_max_attempt_timeout=900*1000; // 15min  
	/** maximum number of consecutive registration authentication attempts before giving up */
	public static int regc_auth_attempts=3;


	// ************************** extensions **************************

	/** Whether forcing this node to stay within the dialog route as peer,
	  * by means of the insertion of a RecordRoute header.
	  * This is a non-standard behaviour and is normally not necessary. */
	public static boolean on_dialog_route=false;

	/** Whether using an alternative transaction id that does not include the 'sent-by' value. */
	//public static boolean alternative_transaction_id=false;


	// ************************** constructor **************************

	/** Parses a single text line (read from the config file) */
	@Override
	protected void parseLine(String line) {
		String attribute;
		Parser par;
		int index=line.indexOf("=");
		if (index>0) {  attribute=line.substring(0,index).trim(); par=new Parser(line,index+1);  }
		else {  attribute=line; par=new Parser("");  }
		char[] delim={' ',','};

		// default sip provider configurations
		if (attribute.equals("default_port")) { default_port=par.getInt(); return; }
		if (attribute.equals("default_tls_port")) { default_tls_port=par.getInt(); return; }
		if (attribute.equals("default_transport_protocols")) { default_transport_protocols=par.getWordArray(delim); return; }
		if (attribute.equals("default_nmax_connections")) { default_nmax_connections=par.getInt(); return; }
		if (attribute.equals("use_rport")) { use_rport=(par.getString().toLowerCase().startsWith("y")); return; }
		if (attribute.equals("force_rport")) { force_rport=(par.getString().toLowerCase().startsWith("y")); return; }

		if (attribute.equals("default_tls_trust_all")){ default_tls_trust_all=(par.getString().toLowerCase().startsWith("y")); return; }
		if (attribute.equals("default_tls_trusted_certs")){ default_tls_trusted_certs=par.getStringArray(); return; }
		if (attribute.equals("default_tls_trust_folder")){ default_tls_trust_folder=par.getRemainingString().trim(); return; }
		if (attribute.equals("default_tls_cert_file")){ default_tls_cert_file=par.getRemainingString().trim(); return; }
		if (attribute.equals("default_tls_key_file")){ default_tls_key_file=par.getRemainingString().trim(); return; }


		// transaction timeouts
		if (attribute.equals("retransmission_timeout")) { retransmission_timeout=par.getInt(); return; }
		if (attribute.equals("max_retransmission_timeout")) { max_retransmission_timeout=par.getInt(); return; }
		if (attribute.equals("transaction_timeout")) { transaction_timeout=par.getInt(); return; }
		if (attribute.equals("clearing_timeout")) { clearing_timeout=par.getInt(); return; }

		// general configurations
		if (attribute.equals("max_forwards"))   { max_forwards=par.getInt(); return; }
		if (attribute.equals("timer_daemon_mode"))   { timer_daemon_mode=(par.getString().toLowerCase().startsWith("y")); return; }
		if (attribute.equals("auto_trying"))    { auto_trying=(par.getString().toLowerCase().startsWith("y")); return; }
		if (attribute.equals("early_dialog"))   { early_dialog=(par.getString().toLowerCase().startsWith("y")); return; }
		if (attribute.equals("default_expires")){ default_expires=par.getInt(); return; }
		if (attribute.equals("ua_info"))        { ua_info=par.getRemainingString().trim(); return; }
		if (attribute.equals("server_info"))    { server_info=par.getRemainingString().trim(); return; }
		if (attribute.equals("supported_option_tags")) { supported_option_tags=par.getWordArray(delim); return; }
		if (attribute.equals("required_option_tags"))  { required_option_tags=par.getWordArray(delim); return; }
		if (attribute.equals("allowed_methods"))       { allowed_methods=par.getWordArray(delim); return; }
		if (attribute.equals("min_session_interval"))  { min_session_interval=par.getInt(); return; }
		if (attribute.equals("default_session_interval"))  { default_session_interval=par.getInt(); return; }

		// registration client configurations
		if (attribute.equals("regc_min_attempt_timeout")) { regc_min_attempt_timeout=par.getInt(); return; }
		if (attribute.equals("regc_max_attempt_timeout")) { regc_max_attempt_timeout=par.getInt(); return; }
		if (attribute.equals("regc_auth_attempts")) { regc_auth_attempts=par.getInt(); return; }

		// extensions
		if (attribute.equals("on_dialog_route")){ on_dialog_route=(par.getString().toLowerCase().startsWith("y")); return; }
		//if (attribute.equals("alternative_transaction_id")){ alternative_transaction_id=(par.getString().toLowerCase().startsWith("y")); return; }

		// old parameters
		if (attribute.equals("host_addr")) LOG.warn("parameter 'host_addr' is no more supported; use 'via_addr' instead.");
		if (attribute.equals("all_interfaces")) LOG.warn("parameter 'all_interfaces' is no more supported; use 'host_iaddr' for setting a specific interface or let it undefined.");
		if (attribute.equals("use_outbound")) LOG.warn("parameter 'use_outbound' is no more supported; use 'outbound_addr' for setting an outbound proxy or let it undefined.");
		if (attribute.equals("log_file")) LOG.warn("parameter 'log_file' is no more supported.");
	}  
		
	/** Converts the entire object into lines (to be saved into the config file) */
	@Override
	protected String toLines() {
		// currently not implemented..
		return "SipStack/"+SipStack.release;
	}
 
	/** Costructs a non-static SipStack */
	private SipConfig() {
		
	}

	/** Inits SipStack */
	public static void init() {
		init(null);
	}

	/** Inits SipStack from the specified <i>file</i> */
	public static void init(String file) {
		
		(new SipConfig()).loadFile(file);
				
		// user-agent info
		if (ua_info!=null && (ua_info.length()==0 || ua_info.equalsIgnoreCase(Configure.NONE) || ua_info.equalsIgnoreCase("NO-UA-INFO"))) ua_info=null;      

		// server info
		if (server_info!=null && (server_info.length()==0 || server_info.equalsIgnoreCase(Configure.NONE) || server_info.equalsIgnoreCase("NO-SERVER-INFO"))) server_info=null;      

		// timers
		Timer.DEFAULT_DAEMON_MODE=timer_daemon_mode;

		is_init=true;
		//if (file!=null) printOut("SipStack loaded",1);
	}

	/** Whether SipStack has been already initialized */
	public static boolean isInit() {
		return is_init;
	}      

}