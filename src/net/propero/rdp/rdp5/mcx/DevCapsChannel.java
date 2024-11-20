/* McxSessChannel.java
 * Component: SoftSled3
 * 
 * Revision: $Revision$
 * Author: $Author$
 * Date: $Date$
 *
 * Copyright (c) 2005 Propero Limited
 *
 * Purpose: 
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 * 
 * (See gpl.txt for details of the GNU General Public License.)
 * 
 */
package net.propero.rdp.rdp5.mcx;

import java.io.*;
import java.util.*;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;

import net.propero.rdp.Common;
import net.propero.rdp.CommunicationMonitor;
import net.propero.rdp.Constants;
import net.propero.rdp.Input;
import net.propero.rdp.Options;
import net.propero.rdp.RdesktopException;
import net.propero.rdp.RdpPacket;
import net.propero.rdp.RdpPacket_Localised;
import net.propero.rdp.Secure;
import net.propero.rdp.crypto.CryptoException;
import net.propero.rdp.rdp5.VChannel;
import net.propero.rdp.rdp5.VChannels;

import org.apache.log4j.Logger;

public class DevCapsChannel extends VChannel implements McxInterface {

	protected static Logger logger = Logger.getLogger(Input.class);
    private int DSPAServiceHandle;

    List<String> enabledDevCaps = Arrays.asList(
//		"2DA", // 2DA - Is 2D animation allowed?
//        "ANI", // ANI - Is intensive animation allowed?
//        "APP", // APP - Is tray applet allowed?
//        "ARA", // ARA - Is auto restart allowed?
        "AUD", // AUD - Is audio allowed?
        "AUR", // AUR - Is audio Non WMP?
//        "BIG", // BIG - Is remote UI renderer big-endian?
//        "BLB", // BLB - Is black letters box needed?
//        "CCC", // CCC - Is CC rendered by the client?
//        "CDA", // CDA - Is CD playback allowed?
//        "CLO", // CLO - Is the close button shown?
//        "CPY", // CPY - Is CD copying allowed?
//        "CRC", // CRC - Is CD burning allowed?
//        "DES", // DES - Is MCE a Windows shell?
//        "DOC", // DOC - Is my Documents populated?
//        "DRC", // DRC - Is DVD burning allowed?
//        "DVD", // DVD - Is DVD playback allowed?
        "EXT", // EXT - Are Extender Settings allowed?
//        "FPD", // FPD - Is FPD allowed?
        "GDI", // GDI - Is GDI renderer used?
        "H02", // H02 - Is 2 feet help allowed? 
        "H10", // H10 - Is 10 feet help allowed? 
        "HDN", // HDN - Is HD content allowed by the network?
        "HDV", // HDV - Is HD content allowed?
        "HTM", // HTM - Is HTML supported?
        //"MAR", // MAR - Are over-scan margins needed?
        "MUT", // MUT - Is mute ui allowed?
//        "NLZ", // NLZ - Is nonlinear zoom supported?
//        "ONS", // ONS - Is online spotlight allowed?
        //"PHO", // PHO - Are advanced photo features allowed?
        "POP", // POP - Are Pop ups allowed?
        "REM", // REM - Is input treated as if from a remote?
        "RSZ", // RSZ - Is raw stretched zoom supported?
//        "RUI", // RUI - Is remote UI rendering supported?
        "SCR", // SCR - Is a native screensaver required?
//        "SDM", // SDM - Is a screen data mode workaround needed?
        "SDN", // SDN - Is SD content allowed by the network?
        "SOU", // SOU - Is UI sound supported?
//        "SUP", // SUP - Is RDP super blt allowed?
//        "SYN", // SYN - Is transfer to a device allowed?
        "TBA", // TBA - Is a Toolbar allowed?
//        "TVS", // TVS - Is a TV skin used?
		"VID", // VID - Is video allowed?
        "VIZ", // VIZ - Is WMP visualisation allowed?
        "VOL", // VOL - Is volume UI allowed?
        "W32", // W32 - Is Win32 content allowed?
        "WE2", // WE2 - Is 2 feet web content allowed? 
        "WEB", // WEB - Is 10 feet web content allowed? 
        "WID", // WID - Is wide screen enabled?
//        "WIN", // WIN - Is window mode allowed?
        "ZOM" // ZOM - Is video zoom mode allowed?
    );

	
	public DevCapsChannel() {

	}

	/*
	 * VChannel inherited abstract methods
	 */
	public String name() {
		return "devcaps";
	}

	public int flags() {
		return VChannels.CHANNEL_OPTION_INITIALIZED
				| VChannels.CHANNEL_OPTION_ENCRYPT_RDP
				| VChannels.CHANNEL_OPTION_COMPRESS_RDP
				| VChannels.CHANNEL_OPTION_SHOW_PROTOCOL;
	}

	/*
	 * Data processing methods
	 */
	public void process(RdpPacket data) throws RdesktopException, IOException,
			CryptoException {
		
		byte[] dataRaw = new byte[data.size() - data.getPosition()];
     	data.copyRemainderToByteArray(dataRaw);
//		System.out.println("DevCaps: Raw "+Arrays.toString(dataRaw));


		int dslrPayloadSize, dslrChildCount, dslrCallingConvention, dslrRequestHandle;
		
		// Get DSLR Request Data
		dslrPayloadSize = data.getBigEndian32();
		dslrChildCount = data.getBigEndian16();
		dslrCallingConvention = data.getBigEndian32();
		dslrRequestHandle = data.getBigEndian32();
		
//		System.out.println("Raw Bytes '" + dslrPayloadSize + "'")
//        System.out.write(createServiceClassID);

//		System.out.println("DevCaps: DSLR - PayloadSize '" + dslrPayloadSize + "'");
//		System.out.println("DevCaps: DSLR - ChildCount '" + dslrChildCount + "'");
//		System.out.println("DevCaps: DSLR - CallingConvention '" + dslrCallingConvention + "'");
//		System.out.println("DevCaps: DSLR - RequestHandle '" + dslrRequestHandle + "'");

		byte[] response = null;
		
		switch (dslrCallingConvention) {
		// DSLR Request
		case 1:
			int dispatchServiceHandle, dispatchFunctionHandle;
			dispatchServiceHandle = data.getBigEndian32();
			dispatchFunctionHandle = data.getBigEndian32();
			
			// Base DSLR Service
			if (dispatchServiceHandle == 0) {
				// Check the DSLR Service Function
				switch (dispatchFunctionHandle) {
				// CreateService Request
				case 0:
					 int createServicePayloadSize, createServiceChildCount, createServiceServiceHandle;
					 UUID createServiceClassID, createServiceServiceID;
					 createServicePayloadSize = data.getBigEndian32();
	                 createServiceChildCount = data.getBigEndian16();
	                 createServiceClassID = data.getUUID();
	                 createServiceServiceID = data.getUUID();
	                 createServiceServiceHandle = data.getBigEndian32();
	                 
	                 switch (createServiceClassID.toString()) {
	                 // If this is the DSPA Service ClassID
	                 case "ef22f459-6b7e-48ba-8838-e2bef821df3c":
	                 	System.out.println("DevCaps: DSLR CreateService (DSPA)");
	                 	DSPAServiceHandle = createServiceServiceHandle;
	                 	response = McxUtilities.createServiceResponse(dslrRequestHandle);
	                 	send_data(response, response.length);
	                 	break;
	                 }

					break;
				// DeleteService Request
				case 1:
					int deleteServicePayloadSize, deleteServiceChildCount, deleteServiceServiceHandle;
					deleteServicePayloadSize = data.getBigEndian32();
					deleteServiceChildCount = data.getBigEndian16();
	                deleteServiceServiceHandle = data.getBigEndian32();
	                // If the DeleteService ServiceHandle is the DSMNServiceHandle
	                if (deleteServiceServiceHandle == DSPAServiceHandle) {
	                 	System.out.println("DevCaps: DSLR DeleteService (DSPA)");
	                	// Clear the DSMN Service
	                    DSPAServiceHandle = 0;
	                } else {
	                 	System.out.println("DevCaps: DSLR DeleteService Handle ("+deleteServiceServiceHandle+") not found");
	                }
					break;
				// Unknown Request
				default:
					break;
				}
			} 
			// DSPA Service
			else if (dispatchServiceHandle == DSPAServiceHandle) {
				// Check the DSPA Service Function
				switch (dispatchFunctionHandle) {
				// CreateService Request
				case 0:
					// Get GetStringProperty Data
                    int GetStringPropertyPayloadSize = data.getBigEndian32();
                    int GetStringPropertyChildCount = data.getBigEndian16();
                    int GetStringPropertyPayloadLength = data.getBigEndian32();
                    String GetStringPropertyPayloadPropertyName = new String(data.getBigEndianVariable(GetStringPropertyPayloadLength), "UTF-8").trim();
					System.out.println("DevCaps: DSPA GetStringProperty '"+GetStringPropertyPayloadPropertyName+"'");
					
					switch (GetStringPropertyPayloadPropertyName) {
					// Property Bag Service
                    case "NAM":
                        // Initialise GetStringProperty Response
                    	response = McxUtilities.getStringPropertyResponse(dslrRequestHandle,
                			"McxClient"
            			);
						send_data(response, response.length);

                        break;
                    case "PRT":
                    	// Initialise GetStringProperty Response
                    	response = McxUtilities.getStringPropertyResponse(dslrRequestHandle,
                			"rtsp-rtp-udp:*:audio/x-ms-wma:DLNA.ORG_PN=WMAFULL;DLNA.ORG_PN=WMAPRO;MICROSOFT.COM_PN=WMALSL\r\n" +
							"rtsp-rtp-udp:*:audio/mpeg:DLNA.ORG_PN=MP3\r\n"+
							"http-get:*:audio/L16:MICROSOFT.COM_PN=WAV_PCM\r\n"+
							"rtsp-rtp-udp:*:video/mpeg:MICROSOFT.COM_PN=DVRMS_MPEG2;MICROSOFT.COM_PN=MPEG4_P2_MP4_ASP_L5_MPEG1_L3;MICROSOFT.COM_PN=MPEG4_P2_AVI_ASP_L5_MPEG1_L3;MICROSOFT.COM_PN=MPEG4_P2_MP4_ASP_L5_AC3;MICROSOFT.COM_PN=MPEG4_P2_AVI_ASP_L5_AC3\r\n"+
							"rtsp-rtp-udp:*:video/x-ms-wmv:DLNA.ORG_PN=WMVHIGH_PRO;MICROSOFT.COM_PN=WMVHIGH_LSL;DLNA.ORG_PN=WMVHIGH_FULL;MICROSOFT.COM_PN=VC1_APL2_FULL;MICROSOFT.COM_PN=VC1_APL2_PRO;MICROSOFT.COM_PN=VC1_APL2_LSL;MICROSOFT.COM_PN=WMVIMAGE1_MED;MICROSOFT.COM_PN=WMVIMAGE2_MED\r\n"+
							"http-get:*:video/mpeg:DLNA.ORG_PN=MPEG1;DLNA.ORG_PN=MPEG_PS_NTSC;DLNA.ORG_PN=MPEG_PS_PAL;DLNA.ORG_PN=MPEG4_P2_TS_ASP_MPEG1_L3;DLNA.ORG_PN=MPEG4_P2_TS_ASP_AC3;DLNA.ORG_PN=MPEG4_P2_TS_ASP_AC3;DLNA.ORG_PN=AVC_MP4_MP_SD_MPEG1_L3;DLNA.ORG_PN=AVC_TS_MP_HD_MPEG1_L3;DLNA.ORG_PN=AVC_MP4_MP_HD_AC3;DLNA.ORG_PN=AVC_MP4_MP_SD_AC3;DLNA.ORG_PN=AVC_TS_MP_HD_AC3"
						);
						send_data(response, response.length);
                        break;
                    case "XTY":
                        // Initialise GetStringProperty Response
                    	response = McxUtilities.getStringPropertyResponse(dslrRequestHandle,
                			"McxClient"
            			);
						send_data(response, response.length);
                        break;
                    case "PBV":
                        // Initialise GetStringProperty Response
                    	response = McxUtilities.getStringPropertyResponse(dslrRequestHandle,
                    		"1"
                        );
						send_data(response, response.length);
                        break;
                    default:
    					System.out.println("DevCaps: DSPA GetStringProperty ("+GetStringPropertyPayloadPropertyName+") not implemented");
                        break;
                }
					
					break;
				case 2:
					// Get GetDWORDProperty Data
					int DWORDPropertyPayloadSize = data.getBigEndian32();
                    int GetDWORDPropertyChildCount = data.getBigEndian16();
                    int GetDWORDPropertyPayloadLength = data.getBigEndian32();
                    String GetDWORDPropertyPayloadPropertyName = new String(data.getBigEndianVariable(GetDWORDPropertyPayloadLength), "UTF-8").trim();
					System.out.println("DevCaps: DSPA GetDWORDProperty '"+GetDWORDPropertyPayloadPropertyName+"'");

					if (enabledDevCaps.contains(GetDWORDPropertyPayloadPropertyName)) {
						response = McxUtilities.DeviceCapabilityTrueGetDWORDPropertyResponse(dslrRequestHandle);
						send_data(response, response.length);
					} else {
						response = McxUtilities.DeviceCapabilityFalseGetDWORDPropertyResponse(dslrRequestHandle);
						send_data(response, response.length);
					}
					
					break;
				default:
					System.out.println("DevCaps: DSPA Function ("+dispatchFunctionHandle+") not implemented");
					break;
				}
			}

			break;
		default:
			System.out.println("DevCaps: DSLR CallingConvention Incorrect '" + dslrCallingConvention + "'");
		}

	}
	
	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for (int j = 0; j < bytes.length; j++) {
	        int v = bytes[j] & 0xFF;  
	 // Interpret as unsigned
	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    }
	    return new String(hexChars);
	}

	public void send_null(int type, int status) {
		RdpPacket_Localised s;

		s = new RdpPacket_Localised(12);
		s.setLittleEndian16(type);
		s.setLittleEndian16(status);
		s.setLittleEndian32(0);
		s.setLittleEndian32(0); // pad
		s.markEnd();

		try {
			this.send_packet(s);
		} catch (RdesktopException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (CryptoException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	

//	void handle_data_request(RdpPacket data) {
//		int format = data.getLittleEndian32();
//		Transferable clipData = clipboard.getContents(this);
//
//		TypeHandler outputHandler = allHandlers.getHandlerForFormat(format);
//		if (outputHandler != null) {
//			outputHandler.send_data(clipData, this);
//			// outData = outputHandler.fromTransferable(clipData);
//			// if(outData != null){
//			// send_data(outData,outData.length);
//			// return;
//			// }
//			// else System.out.println("Clipboard data to send == null!");
//		}
//
//		// this.send_null(CLIPRDR_DATA_RESPONSE,CLIPRDR_ERROR);
//	}
//
//	void handle_data_response(RdpPacket data, int length) {
//		// if(currentHandler !=
//		// null)clipboard.setContents(currentHandler.handleData(data,
//		// length),this);
//		// currentHandler = null;
//		if (currentHandler != null)
//			currentHandler.handleData(data, length, this);
//		currentHandler = null;
//	}

//	void request_clipboard_data(int formatcode) throws RdesktopException,
//			IOException, CryptoException {
//
//		RdpPacket_Localised s = Common.secure.init(
//				Constants.encryption ? Secure.SEC_ENCRYPT : 0, 24);
//		s.setLittleEndian32(16); // length
//
//		int flags = VChannels.CHANNEL_FLAG_FIRST | VChannels.CHANNEL_FLAG_LAST;
//		if ((this.flags() & VChannels.CHANNEL_OPTION_SHOW_PROTOCOL) != 0)
//			flags |= VChannels.CHANNEL_FLAG_SHOW_PROTOCOL;
//
//		s.setLittleEndian32(flags);
//		s.setLittleEndian16(CLIPRDR_DATA_REQUEST);
//		s.setLittleEndian16(CLIPRDR_REQUEST);
//		s.setLittleEndian32(4); // Remaining length
//		s.setLittleEndian32(formatcode);
//		s.setLittleEndian32(0); // Unknown. Garbage pad?
//		s.markEnd();
//
//		Common.secure.send_to_channel(s,
//				Constants.encryption ? Secure.SEC_ENCRYPT : 0, this.mcs_id());
//	}

	public void send_data(byte[] data, int length) {
		CommunicationMonitor.lock(this);

		RdpPacket_Localised all = new RdpPacket_Localised(length);

		all.copyFromByteArray(data, 0, all.getPosition(), length);
		all.incrementPosition(length);
		all.markEnd();

		try {
			this.send_packet(all);
//         	System.out.println("DevCaps: Sent Data");
		} catch (RdesktopException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			if (!Common.underApplet)
				System.exit(-1);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			if (!Common.underApplet)
				System.exit(-1);
		} catch (CryptoException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			if (!Common.underApplet)
				System.exit(-1);
		}

		CommunicationMonitor.unlock(this);
	}

	
	/*
	 * Support methods
	 */
//	private void reset_bool(boolean[] x) {
//		for (int i = 0; i < x.length; i++)
//			x[i] = false;
//	}

}
