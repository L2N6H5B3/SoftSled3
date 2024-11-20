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
import java.util.Iterator;

import net.propero.rdp.Common;
import net.propero.rdp.CommunicationMonitor;
import net.propero.rdp.Constants;
import net.propero.rdp.Input;
import net.propero.rdp.Options;
import net.propero.rdp.Rdesktop;
import net.propero.rdp.RdesktopException;
import net.propero.rdp.RdpPacket;
import net.propero.rdp.RdpPacket_Localised;
import net.propero.rdp.Secure;
import net.propero.rdp.crypto.CryptoException;
import net.propero.rdp.rdp5.VChannel;
import net.propero.rdp.rdp5.VChannels;

import org.apache.log4j.Logger;

public class McxSessChannel extends VChannel implements McxInterface {

	protected static Logger logger = Logger.getLogger(Input.class);
    private int DSMNServiceHandle;


	
	public McxSessChannel() {

	}

	/*
	 * VChannel inherited abstract methods
	 */
	public String name() {
		return "McxSess";
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
//		System.out.println("McxSess: Raw "+Arrays.toString(dataRaw));


		int dslrPayloadSize, dslrChildCount, dslrCallingConvention, dslrRequestHandle;
		
//		byte[] payloadSize = new byte[data.size()];
//		data.copyToByteArray(payloadSize, 0, 0, data.size());
//		byte[] payloadSize = data.getBigEndianVariable(4);
//		byte[] childCount = data.getBigEndianVariable(2);
		
//		int payloadSize = data.getBigEndian32();
//		int childCount =  data.getBigEndian16();
//		System.out.println(payloadSize);
//		System.out.println(childCount);


//		System.out.println("Array Size: " + data.size());
//		System.out.println(Arrays.toString(payloadSize));
//		System.out.println(Arrays.toString(childCount));
//		String hexString = bytesToHex(payloadSize);
//		System.out.println(hexString);
//		System.out.write(childCount);

		
		// Get DSLR Request Data
		dslrPayloadSize = data.getBigEndian32();
		dslrChildCount = data.getBigEndian16();
		dslrCallingConvention = data.getBigEndian32();
		dslrRequestHandle = data.getBigEndian32();
		
//		System.out.println("Raw Bytes '" + dslrPayloadSize + "'")
//        System.out.write(createServiceClassID);

//		System.out.println("McxSess: DSLR - PayloadSize '" + dslrPayloadSize + "'");
//		System.out.println("McxSess: DSLR - ChildCount '" + dslrChildCount + "'");
//		System.out.println("McxSess: DSLR - CallingConvention '" + dslrCallingConvention + "'");
//		System.out.println("McxSess: DSLR - RequestHandle '" + dslrRequestHandle + "'");

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
	                 // If this is the DSMN Service ClassID
	                 case "a30dc60e-1e2c-44f2-bfd1-17e51c0cdf19":
	                 	System.out.println("McxSess: DSLR CreateService (DSMN)");
	                 	DSMNServiceHandle = createServiceServiceHandle;
	                 	byte[] response = McxUtilities.createServiceResponse(dslrRequestHandle);
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
	                if (deleteServiceServiceHandle == DSMNServiceHandle) {
	                 	System.out.println("McxSess: DSLR DeleteService (DSMN)");
	                	// Clear the DSMN Service
	                    DSMNServiceHandle = 0;
	                } else {
	                 	System.out.println("McxSess: DSLR DeleteService Handle ("+deleteServiceServiceHandle+") not found");
	                }
					break;
				// Unknown Request
				default:
					break;
				}
			} 
			// DSMN Service
			else if (dispatchServiceHandle == DSMNServiceHandle) {
				// Check the DSMN Service Function
				switch (dispatchFunctionHandle) {
				// CreateService Request
				case 0:
					// Get ShellDisconnect Data
                    int ShellDisconnectPayloadSize = data.getBigEndian32();
                    int ShellDisconnectChildCount = data.getBigEndian16();
                    int ShellDisconnectPayloadDisconnectReason = data.getBigEndian32();
                    
                    String shellDisconnectReasonString = "";

                    // Set status according to Disconnect Reason
                    switch (ShellDisconnectPayloadDisconnectReason) {
                        case 0:
                            shellDisconnectReasonString = "Disconnected: Shell exited unexpectedly";
                            break;
                        case 1:
                            shellDisconnectReasonString = "Disconnected: Unknown error";
                            break;
                        case 2:
                            shellDisconnectReasonString = "Disconnected: Initialisation error";
                            break;
                        case 3:
                            shellDisconnectReasonString = "Disconnected: Shell is not responding";
                            break;
                        case 4:
                            shellDisconnectReasonString = "Disconnected: Unauthorised UI in the session";
                            break;
                        case 5:
                            shellDisconnectReasonString = "Disconnected: User is not allowed - the remote device was disabled on the host";
                            break;
                        case 6:
                            shellDisconnectReasonString = "Disconnected: Certificate is invalid";
                            break;
                        case 7:
                            shellDisconnectReasonString = "Disconnected: Shell cannot be started";
                            break;
                        case 8:
                            shellDisconnectReasonString = "Disconnected: Shell monitor thread cannot be started";
                            break;
                        case 9:
                            shellDisconnectReasonString = "Disconnected: Message window cannot be created";
                            break;
                        case 10:
                            shellDisconnectReasonString = "Disconnected: Terminal Services session cannot be started";
                            break;
                        case 11:
                            shellDisconnectReasonString = "Disconnected: Plug and Play (PNP) failed";
                            break;
                        case 12:
                            shellDisconnectReasonString = "Disconnected: Certificate is not trusted";
                            break;
                        case 13:
                            shellDisconnectReasonString = "Disconnected: Product regstration is expired";
                            break;
                        case 14:
                            shellDisconnectReasonString = "Disconnected: PC gone to Sleep / Shut Down";
                            break;
                        case 15:
                            shellDisconnectReasonString = "Disconnected: User closed the session";
                            break;
                    }
                    byte[] shellDisconnectResponse = McxUtilities.GenericOKResponse(dslrRequestHandle);
					send_data(shellDisconnectResponse, shellDisconnectResponse.length);
					System.out.println("McxSess: DSMN ShellDisconnect '"+shellDisconnectReasonString+"'");
					break;
				// Heartbeat
				case 1:
					// Get Heartbeat Data
					int HeartbeatPayloadSize = data.getBigEndian32();
                    int HeartbeatChildCount = data.getBigEndian16();
                    int HeartbeatPayloadScreensaverFlag = data.getBigEndian32();
                    
                    byte[] heartbeatResponse = McxUtilities.GenericOKResponse(dslrRequestHandle);
					send_data(heartbeatResponse, heartbeatResponse.length);
					
					System.out.println("McxSess: DSMN Heartbeat");
					break;
				// ShellIsActive
				case 2:
                    byte[] shellIsActiveResponse = McxUtilities.GenericOKResponse(dslrRequestHandle);
					send_data(shellIsActiveResponse, shellIsActiveResponse.length);
					System.out.println("McxSess: DSMN ShellIsActive");
					break;
				// GetQWaveSinkInfo
				case 3:
                    byte[] qwavesinkResponse = McxUtilities.getQWaveSinkInfoResponse(dslrRequestHandle);
					send_data(qwavesinkResponse, qwavesinkResponse.length);
					System.out.println("McxSess: DSMN GetQWaveSinkInfo");
					break;
				default:
//					int UnknownPayloadSize = data.getBigEndian32();
//                  int UnknownChildCount = data.getBigEndian16();
//                  String UnknownPayloadString = new String(data.getBigEndianVariable(UnknownPayloadSize), "UTF-8");
                    
					byte[] response = McxUtilities.GenericOKResponse(dslrRequestHandle);
					send_data(response, response.length);
					System.out.println("McxSess: DSMN Function ("+dispatchFunctionHandle+") not implemented");
					break;
				}
			}

			break;
		default:
			System.out.println("McxSess: DSLR CallingConvention Incorrect '" + dslrCallingConvention + "'");
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
//         	System.out.println("McxSess: Sent Data");
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
