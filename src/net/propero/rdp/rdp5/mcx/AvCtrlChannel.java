/* McxSessChannel.java
 * Component: SoftSled3
 * 
 * Revision: $Revision$
 * Author: $Author$
 * Date: $Date$
 *
 * Copyright (c) 2024 Luke Bradtke
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

public class AvCtrlChannel extends VChannel implements McxInterface {

	protected static Logger logger = Logger.getLogger(Input.class);
    private int DSPAServiceHandle, DMCTServiceHandle, DRMRIServiceHandle;
    private int DMCTRegisterMediaEventCallbackCookie = 14724;
    private int StubRequestHandleIter = 1;
    private int StubServiceHandleIter = 1;
    private Map<Integer, StubRequestType> StubRequestTypeDict = new HashMap<>();
    private Map<Integer, Integer> StubRequestCookieDict = new HashMap<>();
    private Map<Integer, Integer> ProxyRequestHandleDict = new HashMap<>();
    private Map<Integer, Integer> ProxyServiceHandleDict = new HashMap<>();
    
	public AvCtrlChannel() {

	}

	/*
	 * VChannel inherited abstract methods
	 */
	public String name() {
		return "avctrl";
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
		System.out.println("AvCtrl: Raw "+Arrays.toString(dataRaw));


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
	                case "077bfd3a-7028-4913-bd14-53963dc37754":
	                 	System.out.println("AvCtrl: DSLR CreateService (DSPA)");
	                 	DSPAServiceHandle = createServiceServiceHandle;
	                 	response = McxUtilities.createServiceResponse(dslrRequestHandle);
	                 	send_data(response, response.length);
	                 	break;
	                // If this is the DMCT Service ClassID
	                case "18c7c708-c529-4639-a846-5847f31b1e83":
	                 	System.out.println("AvCtrl: DSLR CreateService (DMCT)");
	                 	DMCTServiceHandle = createServiceServiceHandle;
	                 	response = McxUtilities.createServiceResponse(dslrRequestHandle);
	                 	send_data(response, response.length);
	                 	break;
	                // If this is the DRMRI Service ClassID
	                case "b707af79-ca99-42d1-8c60-469fe112001e":
	                 	System.out.println("AvCtrl: DSLR CreateService (DRMRI)");
	                 	DRMRIServiceHandle = createServiceServiceHandle;
	                 	response = McxUtilities.createServiceResponse(dslrRequestHandle);
	                 	send_data(response, response.length);
	                 	break;
                 	default:
 	                 	System.out.println("AvCtrl: DSLR CreateService ("+createServiceClassID.toString()+") not implemented");
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
	                 	System.out.println("AvCtrl: DSLR DeleteService (DSPA)");
	                	// Clear the DSPA Service
	                    DSPAServiceHandle = 0;
	                } else if (deleteServiceServiceHandle == DMCTServiceHandle) {
	                 	System.out.println("AvCtrl: DSLR DeleteService (DMCT)");
	                	// Clear the DMCT Service
	                    DMCTServiceHandle = 0;
	                } else if (deleteServiceServiceHandle == DRMRIServiceHandle) {
	                 	System.out.println("AvCtrl: DSLR DeleteService (DRMRI)");
	                	// Clear the DRMRI Service
	                    DRMRIServiceHandle = 0;
	                } else {
	                 	System.out.println("AvCtrl: DSLR DeleteService Handle ("+deleteServiceServiceHandle+") not found");
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
					System.out.println("AvCtrl: DSPA GetStringProperty '"+GetStringPropertyPayloadPropertyName+"'");
					
					switch (GetStringPropertyPayloadPropertyName) {
					// Property Bag Service
                    case "XspHostAddress":
                        // Initialise GetStringProperty Response
                    	response = McxUtilities.getStringPropertyResponse(dslrRequestHandle,
                			"10.1.1.33"
            			);
						send_data(response, response.length);

                        break;
                    default:
    					System.out.println("AvCtrl: DSPA GetStringProperty ("+GetStringPropertyPayloadPropertyName+") not implemented");
                        break;
					}	
					break;
				case 2:
					// Get GetDWORDProperty Data
					int GetDWORDPropertyPayloadSize = data.getBigEndian32();
                    int GetDWORDPropertyChildCount = data.getBigEndian16();
                    int GetDWORDPropertyPayloadLength = data.getBigEndian32();
                    String GetDWORDPropertyPayloadPropertyName = new String(data.getBigEndianVariable(GetDWORDPropertyPayloadLength), "UTF-8").trim();
					System.out.println("AvCtrl: DSPA GetDWORDProperty '"+GetDWORDPropertyPayloadPropertyName+"'");

					switch (GetDWORDPropertyPayloadPropertyName) {
                    case "IsMuted":
                    	response = McxUtilities.GetDWORDPropertyResponse(dslrRequestHandle, 0);
						send_data(response, response.length);

                        break;
                    case "Volume":
                    	response = McxUtilities.GetDWORDPropertyResponse(dslrRequestHandle, 1);
						send_data(response, response.length);
                    	break;
                    default:
    					System.out.println("AvCtrl: DSPA GetDWORDProperty ("+GetDWORDPropertyPayloadPropertyName+") not implemented");
                        break;
					}	
					break;
				case 3:
					// Set GetDWORDProperty Data
					int SetDWORDPropertyPayloadSize = data.getBigEndian32();
                    int SetDWORDPropertyChildCount = data.getBigEndian16();
                    int SetDWORDPropertyPayloadLength = data.getBigEndian32();
                    String SetDWORDPropertyPayloadPropertyName = new String(data.getBigEndianVariable(SetDWORDPropertyPayloadLength), "UTF-8").trim();
					int SetDWORDPropertyPayloadPropertyValue = data.getBigEndian32();
                    System.out.println("AvCtrl: DSPA GetDWORDProperty '"+SetDWORDPropertyPayloadPropertyName+"'");

					switch (SetDWORDPropertyPayloadPropertyName) {
                    case "IsMuted":
                    	response = McxUtilities.GenericOKResponse(dslrRequestHandle);
						send_data(response, response.length);
                        break;
                    default:
    					System.out.println("AvCtrl: DSPA SetDWORDProperty ("+SetDWORDPropertyPayloadPropertyName+") not implemented");
                        break;
					}	
					break;
				default:
					System.out.println("AvCtrl: DSPA Function ("+dispatchFunctionHandle+") not implemented");
					break;
				}
			}
			// DMCT Service
			else if (dispatchServiceHandle == DMCTServiceHandle) {
				// Check the DMCT Service Function
				switch (dispatchFunctionHandle) {
				// OpenMedia Request
				case 0:
					// Get OpenMedia Data
					int OpenMediaPayloadSize =  data.getBigEndian32();
                    int OpenMediaChildCount = data.getBigEndian16();
                    int OpenMediaPayloadURLLength = data.getBigEndian32();
                    String OpenMediaPayloadURL = new String(data.getBigEndianVariable(OpenMediaPayloadURLLength), "UTF-8").trim();
                    int OpenMediaPayloadSurfaceID = data.getBigEndian32();
                    int OpenMediaPayloadTimeOut = data.getBigEndian32();
					System.out.println("AvCtrl: DMCT OpenMedia '"+OpenMediaPayloadURL+"'");
					response = McxUtilities.GenericOKResponse(dslrRequestHandle);
					send_data(response, response.length);
					break;
				case 1:
					System.out.println("AvCtrl: DMCT CloseMedia");
					response = McxUtilities.GenericOKResponse(dslrRequestHandle);
					send_data(response, response.length);
					break;
				case 2:
					// Get StartMedia Data
					int StartPayloadSize = data.getBigEndian32();
                    int StartChildCount = data.getBigEndian16();
                    int StartPayloadStartTime = data.get8();
                    int StartPayloadUseOptimisedPreroll = data.get8();
                	int StartPayloadRequestedPlayRate = data.getBigEndian32();
                	int StartPayloadAvailableBandwidth = data.get8();

					System.out.println("AvCtrl: DMCT StartMedia");
					
					response = McxUtilities.startResponse(dslrRequestHandle, 1);
					send_data(response, response.length);
					break;
				case 3:
					System.out.println("AvCtrl: DMCT PauseMedia");
					response = McxUtilities.GenericOKResponse(dslrRequestHandle);
					send_data(response, response.length);
					break;
				case 4:
					System.out.println("AvCtrl: DMCT StopMedia");
					response = McxUtilities.GenericOKResponse(dslrRequestHandle);
					send_data(response, response.length);
					break;
				case 5:
					System.out.println("AvCtrl: DMCT GetDuration");
					response = McxUtilities.getDurationResponse(dslrRequestHandle, 100000);
					send_data(response, response.length);
					break;
				case 6:
					System.out.println("AvCtrl: DMCT GetPosition");
					response = McxUtilities.getDurationResponse(dslrRequestHandle, 20000);
					send_data(response, response.length);
					break;
				case 8:
					int RegisterMediaEventCallbackPayloadSize = data.getBigEndian32();
                    int RegisterMediaEventCallbackChildCount = data.getBigEndian16();
                    UUID RegisterMediaEventCallbackClassID = data.getUUID();
                    UUID RegisterMediaEventCallbackServiceID = data.getUUID();

                    // Add Stub RequestType to Dictionary
                    StubRequestTypeDict.put(StubRequestHandleIter, StubRequestType.RegisterMediaEventCallback);
                    // Add Proxy RequestHandle Iter to Stub RequestHandle Match Dictionary
                    ProxyRequestHandleDict.put(StubRequestHandleIter, dslrRequestHandle);
                    // Add Proxy Service Handle to Dictionary
                    ProxyServiceHandleDict.put(StubRequestHandleIter, StubServiceHandleIter);
                    
                    // Initialise RegisterMediaEventCallback CreateService Request
                    response = McxUtilities.createServiceRequest(StubRequestHandleIter, RegisterMediaEventCallbackClassID, RegisterMediaEventCallbackServiceID, StubServiceHandleIter);
					send_data(response, response.length);

					System.out.println("CreateServiceRequestBytes "+Arrays.toString(response));

					
                    // Increment Stub RequestHandle Iter
                    StubRequestHandleIter++;
                    // Increment Stub ServicesHandle Iter
                    StubServiceHandleIter++;
                    
					System.out.println("AvCtrl: DMCT RegisterMediaEventCallback");
					break;
				case 9:
					int UnregisterMediaEventCallbackPayloadSize = data.getBigEndian32();
                    int UnregisterMediaEventCallbackChildCount = data.getBigEndian16();
                    int UnregisterMediaEventCallbackPayloadCookie = data.getBigEndian32();
                    
                    // Add Stub RequestHandle Iter to Dictionary
                    StubRequestTypeDict.put(StubRequestHandleIter, StubRequestType.UnregisterMediaEventCallback);

                    // Add Proxy RequestHandle Iter to Stub RequestHandle Match Dictionary
                    ProxyRequestHandleDict.put(StubRequestHandleIter, dslrRequestHandle);
                    
                    // Initialise RegisterMediaEventCallback DeleteService Request
                    response = McxUtilities.deleteServiceRequest(StubRequestHandleIter, StubRequestCookieDict.get(UnregisterMediaEventCallbackPayloadCookie));
					send_data(response, response.length);
					                    
					// Increment Stub RequestHandle Iter
                    StubRequestHandleIter++;
                    
					System.out.println("AvCtrl: DMCT UnregisterMediaEventCallback");
					break;
				default:
					System.out.println("AvCtrl: DMCT Function ("+dispatchFunctionHandle+") not implemented");
					break;
				}
			} // DRMRI Service
			else if (dispatchServiceHandle == DRMRIServiceHandle) {
				// Check the DRMRI Service Function
				switch (dispatchFunctionHandle) {
				// RegisterTransmitterService Request
				case 0:
					// Get RegisterTransmitterService Data
					int RegisterTransmitterServicePayloadSize =  data.getBigEndian32();
                    int RegisterTransmitterServiceChildCount = data.getBigEndian16();
                    UUID RegisterTransmitterServiceClassID = data.getUUID();
                    
					System.out.println("AvCtrl: DRMRI RegisterTransmitterService");
					
					response = McxUtilities.registerTransmitterServiceResponse(dslrRequestHandle);
					send_data(response, response.length);
					break;
				// UnregisterTransmitterService Request
				case 1:
					System.out.println("AvCtrl: DRMRI UnregisterTransmitterService");
					response = McxUtilities.GenericOKResponse(dslrRequestHandle);
					send_data(response, response.length);
					break;
				case 2:
					System.out.println("AvCtrl: DRMRI IntiateRegistration");
					response = McxUtilities.GenericOKResponse(dslrRequestHandle);
					send_data(response, response.length);
					break;
				default:
					System.out.println("AvCtrl: DRMRI Function ("+dispatchFunctionHandle+") not implemented");
					break;
				}
			}
			break;
		// DMCT Callback Response
		case 2:
			switch (StubRequestTypeDict.get(dslrRequestHandle)) {
			case StubRequestType.RegisterMediaEventCallback:
				// Get RegisterMediaEventCallback CreateService Response Data
                int RegisterMediaEventCallbackPayloadSize = data.getBigEndian32();
                int RegisterMediaEventCallbackChildCount = data.getBigEndian16();
                int RegisterMediaEventCallbackPayloadResult = data.getBigEndian32();
                
                if (RegisterMediaEventCallbackPayloadResult != 0) {
					System.out.println("AvCtrl: DMCT RegisterMediaEventCallback Request Failed Response");
                }
                
                // Get Proxy Service Handle from Dictionary and Add with Cookie
                StubRequestCookieDict.put(DMCTRegisterMediaEventCallbackCookie, ProxyServiceHandleDict.get(dslrRequestHandle));
                
                // Initialise RegisterMediaEventCallback CreateService Response
                response = McxUtilities.registerMediaEventCallbackResponse(
                        ProxyRequestHandleDict.get(dslrRequestHandle),
                        DMCTRegisterMediaEventCallbackCookie,
                        RegisterMediaEventCallbackPayloadResult
                );
				send_data(response, response.length);

				 // Increment Cookie
                DMCTRegisterMediaEventCallbackCookie++;
                
				System.out.println("AvCtrl: DMCT RegisterMediaEventCallbackResponse");

				break;
			case StubRequestType.UnregisterMediaEventCallback:
				// Get RegisterMediaEventCallback CreateService Response Data
                int UnregisterMediaEventCallbackPayloadSize = data.getBigEndian32();
                int UnregisterMediaEventCallbackChildCount = data.getBigEndian16();
                int UnregisterMediaEventCallbackPayloadResult = data.getBigEndian32();
                
                if (UnregisterMediaEventCallbackPayloadResult != 0) {
					System.out.println("AvCtrl: DMCT UnregisterMediaEventCallback Request Failed Response");
                }
                
                // Initialise RegisterMediaEventCallback DeleteService Response
                response = McxUtilities.unregisterMediaEventCallbackResponse(
                        ProxyRequestHandleDict.get(dslrRequestHandle),
                        UnregisterMediaEventCallbackPayloadResult
                );
				send_data(response, response.length);

				 // Increment Cookie
                DMCTRegisterMediaEventCallbackCookie++;
                
				break;
			}
			
		default:
			System.out.println("AvCtrl: DSLR CallingConvention Incorrect '" + dslrCallingConvention + "'");
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
         	System.out.println("AvCtrl: Sent Data");
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
	
	public enum StubRequestType {
	    RegisterMediaEventCallback,
	    UnregisterMediaEventCallback
	}

}
