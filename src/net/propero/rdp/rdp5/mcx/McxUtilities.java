package net.propero.rdp.rdp5.mcx;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;

public class McxUtilities {
	
	//region DSLR Functions ###################################################

	public static byte[] createServiceRequest(int dispatchRequestHandleInt, UUID dispatchRequestClassIdUUID, UUID dispatchRequestServiceIdUUID, int serviceHandleInt) {
    	
    	ByteBuffer dispatchRequestHandleBuffer = ByteBuffer.allocate(4);
    	dispatchRequestHandleBuffer.order(ByteOrder.LITTLE_ENDIAN);
    	dispatchRequestHandleBuffer.putInt(dispatchRequestHandleInt);
    	byte[] dispatchRequestHandle = dispatchRequestHandleBuffer.array();
    	for (int i = 0; i < dispatchRequestHandle.length / 2; i++) {
	        byte temp = dispatchRequestHandle[i];
	        dispatchRequestHandle[i] = dispatchRequestHandle[dispatchRequestHandle.length - i - 1];
	        dispatchRequestHandle[dispatchRequestHandle.length - i - 1] = temp;
		}
    	
    	ByteBuffer dispatchRequestClassIdBuffer = ByteBuffer.wrap(new byte[16]);
    	dispatchRequestClassIdBuffer.putLong(dispatchRequestClassIdUUID.getMostSignificantBits());
    	dispatchRequestClassIdBuffer.putLong(dispatchRequestClassIdUUID.getLeastSignificantBits());
        byte[] dispatchRequestClassId = dispatchRequestClassIdBuffer.array();
        
        ByteBuffer dispatchRequestServiceIdBuffer = ByteBuffer.wrap(new byte[16]);
        dispatchRequestServiceIdBuffer.putLong(dispatchRequestServiceIdUUID.getMostSignificantBits());
    	dispatchRequestServiceIdBuffer.putLong(dispatchRequestServiceIdUUID.getLeastSignificantBits());
        byte[] dispatchRequestServiceId = dispatchRequestServiceIdBuffer.array();
        
//        ByteBuffer serviceHandleBuffer = ByteBuffer.allocate(4);
//        serviceHandleBuffer.order(ByteOrder.LITTLE_ENDIAN);
//        serviceHandleBuffer.putInt(serviceHandleInt);
//    	byte[] serviceHandle = serviceHandleBuffer.array();
//    	for (int i = 0; i < serviceHandle.length / 2; i++) {
//	        byte temp = serviceHandle[i];
//	        serviceHandle[i] = serviceHandle[serviceHandle.length - i - 1];
//	        serviceHandle[serviceHandle.length - i - 1] = temp;
//		}
        
        byte[] dispatchServiceHandle = new byte[]{0, 0, 0, 0};
        byte[] dispatchFunctionHandle = new byte[]{0, 0, 0, 0};

    	
        // Get Dispatch Byte Arrays
        byte[] dispatchPayloadSize = getInverse4ByteArrayFromInt(
                4 + 
                dispatchRequestHandle.length + 
                dispatchServiceHandle.length + 
                dispatchFunctionHandle.length
        );
        byte[] dispatchChildCount = new byte[]{0, 1};
        byte[] dispatchCallingConvention = new byte[]{0, 0, 0, 1};

        // Get CreateService Byte Arrays
        byte[] createServiceChildCount = new byte[]{0, 0};
        byte[] CreateServicePayloadClassID = dispatchRequestClassId;
        byte[] CreateServicePayloadServiceID = dispatchRequestServiceId;
        byte[] CreateServicePayloadServiceHandle = getInverse4ByteArrayFromInt(serviceHandleInt);
        
        byte[] CreateServicePropertyPayloadSize = getInverse4ByteArrayFromInt(
        		dispatchRequestClassId.length +
        		CreateServicePayloadServiceID.length +
                CreateServicePayloadServiceHandle.length
        );
        
        // Formulate full response using Java's ByteBuffer for efficiency
        ByteBuffer response = ByteBuffer.allocate(
                dispatchPayloadSize.length +
                dispatchChildCount.length +
                dispatchCallingConvention.length +
                dispatchRequestHandle.length +
                dispatchServiceHandle.length +
                dispatchFunctionHandle.length +
                CreateServicePropertyPayloadSize.length +
                createServiceChildCount.length +
                CreateServicePayloadClassID.length + 
                CreateServicePayloadServiceID.length + 
                CreateServicePayloadServiceHandle.length

        );
        response.order(ByteOrder.LITTLE_ENDIAN);
        // Add the byte arrays in the correct order
        response.put(dispatchPayloadSize);
        response.put(dispatchChildCount);
        response.put(dispatchCallingConvention);
        response.put(dispatchRequestHandle);
        response.put(dispatchServiceHandle);
        response.put(dispatchFunctionHandle);
        response.put(CreateServicePropertyPayloadSize);
        response.put(createServiceChildCount);
        response.put(CreateServicePayloadClassID);
        response.put(CreateServicePayloadServiceID);
        response.put(CreateServicePayloadServiceHandle);

        // Return the created byte array
        return response.array();
    }
	
	
    public static byte[] createServiceResponse(int dispatchRequestHandleInt) {
    	
    	ByteBuffer dispatchRequestHandleBuffer = ByteBuffer.allocate(4);
    	dispatchRequestHandleBuffer.order(ByteOrder.LITTLE_ENDIAN);
    	dispatchRequestHandleBuffer.putInt(dispatchRequestHandleInt);
    	byte[] dispatchRequestHandle = dispatchRequestHandleBuffer.array();
    	for (int i = 0; i < dispatchRequestHandle.length / 2; i++) {
	        byte temp = dispatchRequestHandle[i];
	        dispatchRequestHandle[i] = dispatchRequestHandle[dispatchRequestHandle.length - i - 1];
	        dispatchRequestHandle[dispatchRequestHandle.length - i - 1] = temp;
		}
    	
        // Get Dispatch Byte Arrays
        byte[] dispatchPayloadSize = getInverse4ByteArrayFromInt(
                4 + 
                dispatchRequestHandle.length
        );
        byte[] dispatchChildCount = new byte[]{0, 1};
        byte[] dispatchCallingConvention = new byte[]{0, 0, 0, 2};

        // Get CreateService Byte Arrays
        byte[] createServicePayloadSize = new byte[]{0, 0, 0, 4};
        byte[] createServiceChildCount = new byte[]{0, 0};
        byte[] createServicePayloadS_OK = new byte[]{0, 0, 0, 0};

        // Formulate full response using Java's ByteBuffer for efficiency
        ByteBuffer response = ByteBuffer.allocate(
                dispatchPayloadSize.length +
                dispatchChildCount.length +
                dispatchCallingConvention.length +
                dispatchRequestHandle.length +
                createServicePayloadSize.length +
                createServiceChildCount.length +
                createServicePayloadS_OK.length
        );
        response.order(ByteOrder.LITTLE_ENDIAN);
        // Add the byte arrays in the correct order
        response.put(dispatchPayloadSize);
        response.put(dispatchChildCount);
        response.put(dispatchCallingConvention);
        response.put(dispatchRequestHandle);
        response.put(createServicePayloadSize);
        response.put(createServiceChildCount);
        response.put(createServicePayloadS_OK);

        // Return the created byte array
        return response.array();
    }
    
    
	public static byte[] deleteServiceRequest(int dispatchRequestHandleInt, int serviceHandleInt) {
    	
    	ByteBuffer dispatchRequestHandleBuffer = ByteBuffer.allocate(4);
    	dispatchRequestHandleBuffer.order(ByteOrder.LITTLE_ENDIAN);
    	dispatchRequestHandleBuffer.putInt(dispatchRequestHandleInt);
    	byte[] dispatchRequestHandle = dispatchRequestHandleBuffer.array();
    	for (int i = 0; i < dispatchRequestHandle.length / 2; i++) {
	        byte temp = dispatchRequestHandle[i];
	        dispatchRequestHandle[i] = dispatchRequestHandle[dispatchRequestHandle.length - i - 1];
	        dispatchRequestHandle[dispatchRequestHandle.length - i - 1] = temp;
		}
    	
        byte[] dispatchServiceHandle = new byte[]{0, 0, 0, 0};
        byte[] dispatchFunctionHandle = new byte[]{0, 0, 0, 0};
        // Get Dispatch Byte Arrays
        byte[] dispatchPayloadSize = getInverse4ByteArrayFromInt(
                4 + 
                dispatchRequestHandle.length + 
                dispatchServiceHandle.length + 
                dispatchFunctionHandle.length
        );
        byte[] dispatchChildCount = new byte[]{0, 1};
        byte[] dispatchCallingConvention = new byte[]{0, 0, 0, 1};

        // Get CreateService Byte Arrays
        byte[] createServiceChildCount = new byte[]{0, 0};
        byte[] CreateServicePayloadServiceHandle = getInverse4ByteArrayFromInt(serviceHandleInt);
        
        byte[] CreateServicePropertyPayloadSize = getInverse4ByteArrayFromInt(
                CreateServicePayloadServiceHandle.length
        );
        
        // Formulate full response using Java's ByteBuffer for efficiency
        ByteBuffer response = ByteBuffer.allocate(
                dispatchPayloadSize.length +
                dispatchChildCount.length +
                dispatchCallingConvention.length +
                dispatchRequestHandle.length +
                dispatchServiceHandle.length +
                dispatchFunctionHandle.length +
                CreateServicePropertyPayloadSize.length +
                createServiceChildCount.length +
                CreateServicePayloadServiceHandle.length

        );
        response.order(ByteOrder.LITTLE_ENDIAN);
        // Add the byte arrays in the correct order
        response.put(dispatchPayloadSize);
        response.put(dispatchChildCount);
        response.put(dispatchCallingConvention);
        response.put(dispatchRequestHandle);
        response.put(dispatchServiceHandle);
        response.put(dispatchFunctionHandle);
        response.put(CreateServicePropertyPayloadSize);
        response.put(createServiceChildCount);
        response.put(CreateServicePayloadServiceHandle);

        // Return the created byte array
        return response.array();
    }
	
	//endregion ###############################################################
	
	
	//region DMCT Functions ###################################################
	
	public static byte[] registerMediaEventCallbackResponse(int dispatchRequestHandleInt, int cookieInt, int registerMediaEventCallbackPayloadResultInt) {

		ByteBuffer dispatchRequestHandleBuffer = ByteBuffer.allocate(4);
    	dispatchRequestHandleBuffer.order(ByteOrder.LITTLE_ENDIAN);
    	dispatchRequestHandleBuffer.putInt(dispatchRequestHandleInt);
    	byte[] dispatchRequestHandle = dispatchRequestHandleBuffer.array();
    	for (int i = 0; i < dispatchRequestHandle.length / 2; i++) {
	        byte temp = dispatchRequestHandle[i];
	        dispatchRequestHandle[i] = dispatchRequestHandle[dispatchRequestHandle.length - i - 1];
	        dispatchRequestHandle[dispatchRequestHandle.length - i - 1] = temp;
		}

    	
    	
        // Get Dispatch Byte Arrays
        byte[] dispatchPayloadSize = getInverse4ByteArrayFromInt(
                4 + dispatchRequestHandle.length
        );
        byte[] dispatchChildCount = new byte[]{0, 1};
        byte[] dispatchCallingConvention = new byte[]{0, 0, 0, 2};

        // Get RegisterMediaEventCallback Byte Arrays
        byte[] registerMediaEventCallbackChildCount = new byte[]{0, 0};
        byte[] registerMediaEventCallbackPayloadResult = getInverse4ByteArrayFromInt(registerMediaEventCallbackPayloadResultInt);
        byte[] registerMediaEventCallbackPayloadCookie = getInverse4ByteArrayFromInt(cookieInt);
        byte[] registerMediaEventCallbackPropertyPayloadSize = getInverse4ByteArrayFromInt(
                registerMediaEventCallbackPayloadResult.length +
                registerMediaEventCallbackPayloadCookie.length
        );

        // Formulate full response using ByteBuffer
        ByteBuffer response = ByteBuffer.allocate(
                dispatchPayloadSize.length +
                dispatchChildCount.length +
                dispatchCallingConvention.length +
                dispatchRequestHandle.length +
                registerMediaEventCallbackPropertyPayloadSize.length +
                registerMediaEventCallbackChildCount.length +
                registerMediaEventCallbackPayloadResult.length +
                registerMediaEventCallbackPayloadCookie.length
        );

        response.put(dispatchPayloadSize);
        response.put(dispatchChildCount);
        response.put(dispatchCallingConvention);
        response.put(dispatchRequestHandle);
        response.put(registerMediaEventCallbackPropertyPayloadSize);
        response.put(registerMediaEventCallbackChildCount);
        response.put(registerMediaEventCallbackPayloadResult);
        response.put(registerMediaEventCallbackPayloadCookie);

        return response.array();
    }

    public static byte[] unregisterMediaEventCallbackResponse(int dispatchRequestHandleInt, int unregisterMediaEventCallbackPayloadResultInt) {

    	ByteBuffer dispatchRequestHandleBuffer = ByteBuffer.allocate(4);
    	dispatchRequestHandleBuffer.order(ByteOrder.LITTLE_ENDIAN);
    	dispatchRequestHandleBuffer.putInt(dispatchRequestHandleInt);
    	byte[] dispatchRequestHandle = dispatchRequestHandleBuffer.array();
    	for (int i = 0; i < dispatchRequestHandle.length / 2; i++) {
	        byte temp = dispatchRequestHandle[i];
	        dispatchRequestHandle[i] = dispatchRequestHandle[dispatchRequestHandle.length - i - 1];
	        dispatchRequestHandle[dispatchRequestHandle.length - i - 1] = temp;
		}

        // Get Dispatch Byte Arrays
        byte[] dispatchPayloadSize = getInverse4ByteArrayFromInt(
                4 + dispatchRequestHandle.length
        );
        byte[] dispatchChildCount = new byte[]{0, 1};
        byte[] dispatchCallingConvention = new byte[]{0, 0, 0, 2};

        // Get UnregisterMediaEventCallback Byte Arrays
        byte[] unregisterMediaEventCallbackChildCount = new byte[]{0, 0};
        byte[] unregisterMediaEventCallbackPayloadResult = getInverse4ByteArrayFromInt(unregisterMediaEventCallbackPayloadResultInt);
        byte[] unregisterMediaEventCallbackPropertyPayloadSize = getInverse4ByteArrayFromInt(
        		unregisterMediaEventCallbackPayloadResult.length
        );

        // Formulate full response using ByteBuffer
        ByteBuffer response = ByteBuffer.allocate(
                dispatchPayloadSize.length +
                dispatchChildCount.length +
                dispatchCallingConvention.length +
                dispatchRequestHandle.length +
                unregisterMediaEventCallbackPropertyPayloadSize.length +
                unregisterMediaEventCallbackChildCount.length +
                unregisterMediaEventCallbackPayloadResult.length
        );

        response.put(dispatchPayloadSize);
        response.put(dispatchChildCount);
        response.put(dispatchCallingConvention);
        response.put(dispatchRequestHandle);
        response.put(unregisterMediaEventCallbackPropertyPayloadSize);
        response.put(unregisterMediaEventCallbackChildCount);
        response.put(unregisterMediaEventCallbackPayloadResult);

        return response.array();
    }
    
    public static byte[] startResponse(int dispatchRequestHandleInt, int grantedPlayRateInt) {
    	
    	ByteBuffer dispatchRequestHandleBuffer = ByteBuffer.allocate(4);
    	dispatchRequestHandleBuffer.order(ByteOrder.LITTLE_ENDIAN);
    	dispatchRequestHandleBuffer.putInt(dispatchRequestHandleInt);
    	byte[] dispatchRequestHandle = dispatchRequestHandleBuffer.array();
    	for (int i = 0; i < dispatchRequestHandle.length / 2; i++) {
	        byte temp = dispatchRequestHandle[i];
	        dispatchRequestHandle[i] = dispatchRequestHandle[dispatchRequestHandle.length - i - 1];
	        dispatchRequestHandle[dispatchRequestHandle.length - i - 1] = temp;
		}
    	
        // Get Dispatch Byte Arrays
        byte[] dispatchPayloadSize = getInverse4ByteArrayFromInt(
                4 + dispatchRequestHandle.length
        );
        byte[] dispatchChildCount = new byte[]{0, 1};
        byte[] dispatchCallingConvention = new byte[]{0, 0, 0, 2};

        // Get Start Byte Arrays
        byte[] startChildCount = new byte[]{0, 0};
        byte[] startPayloadS_OK = new byte[]{0, 0, 0, 0};
        byte[] startPayloadGrantedPlayRate = getInverse4ByteArrayFromInt(grantedPlayRateInt);
        byte[] startPropertyPayloadSize = getInverse4ByteArrayFromInt(
                startPayloadS_OK.length +
                        startPayloadGrantedPlayRate.length
        );

        // Formulate full response using ByteBuffer
        ByteBuffer response = ByteBuffer.allocate(
                dispatchPayloadSize.length +
                        dispatchChildCount.length +
                        dispatchCallingConvention.length +
                        dispatchRequestHandle.length +
                        startPropertyPayloadSize.length +
                        startChildCount.length +
                        startPayloadS_OK.length +
                        startPayloadGrantedPlayRate.length
        );

        response.put(dispatchPayloadSize);
        response.put(dispatchChildCount);
        response.put(dispatchCallingConvention);
        response.put(dispatchRequestHandle);
        response.put(startPropertyPayloadSize);
        response.put(startChildCount);
        response.put(startPayloadS_OK);
        response.put(startPayloadGrantedPlayRate);

        return response.array();
    }
    
    public static byte[] getDurationResponse(int dispatchRequestHandleInt, long durationLong) {

    	ByteBuffer dispatchRequestHandleBuffer = ByteBuffer.allocate(4);
    	dispatchRequestHandleBuffer.order(ByteOrder.LITTLE_ENDIAN);
    	dispatchRequestHandleBuffer.putInt(dispatchRequestHandleInt);
    	byte[] dispatchRequestHandle = dispatchRequestHandleBuffer.array();
    	for (int i = 0; i < dispatchRequestHandle.length / 2; i++) {
	        byte temp = dispatchRequestHandle[i];
	        dispatchRequestHandle[i] = dispatchRequestHandle[dispatchRequestHandle.length - i - 1];
	        dispatchRequestHandle[dispatchRequestHandle.length - i - 1] = temp;
		}
    	
        // Get Dispatch Byte Arrays
        byte[] dispatchPayloadSize = getInverse4ByteArrayFromInt(
                4 + dispatchRequestHandle.length
        );
        byte[] dispatchChildCount = new byte[]{0, 1};
        byte[] dispatchCallingConvention = new byte[]{0, 0, 0, 2};

        // Get GetDuration Byte Arrays
        byte[] getDurationChildCount = new byte[]{0, 0};
        byte[] getDurationPayloadS_OK = new byte[]{0, 0, 0, 0};
        byte[] getDurationPayloadDuration = getInverse8ByteArrayFromLong(durationLong);
        byte[] getDurationPropertyPayloadSize = getInverse4ByteArrayFromInt(
                getDurationPayloadS_OK.length +
                        getDurationPayloadDuration.length
        );

        // Formulate full response using ByteBuffer
        ByteBuffer response = ByteBuffer.allocate(
                dispatchPayloadSize.length +
                dispatchChildCount.length +
                dispatchCallingConvention.length +
                dispatchRequestHandle.length +
                getDurationPropertyPayloadSize.length +
                getDurationChildCount.length +
                getDurationPayloadS_OK.length +
                getDurationPayloadDuration.length
        );

        response.put(dispatchPayloadSize);
        response.put(dispatchChildCount);
        response.put(dispatchCallingConvention);
        response.put(dispatchRequestHandle);
        response.put(getDurationPropertyPayloadSize);
        response.put(getDurationChildCount);
        response.put(getDurationPayloadS_OK);
        response.put(getDurationPayloadDuration);

        return response.array();
    }
    
	//endregion ###############################################################

    
	//region DSPA Functions ###################################################
    
    public static byte[] DeviceCapabilityTrueGetDWORDPropertyResponse(int dispatchRequestHandleInt) {

    	ByteBuffer dispatchRequestHandleBuffer = ByteBuffer.allocate(4);
    	dispatchRequestHandleBuffer.order(ByteOrder.LITTLE_ENDIAN);
    	dispatchRequestHandleBuffer.putInt(dispatchRequestHandleInt);
    	byte[] dispatchRequestHandle = dispatchRequestHandleBuffer.array();
    	for (int i = 0; i < dispatchRequestHandle.length / 2; i++) {
	        byte temp = dispatchRequestHandle[i];
	        dispatchRequestHandle[i] = dispatchRequestHandle[dispatchRequestHandle.length - i - 1];
	        dispatchRequestHandle[dispatchRequestHandle.length - i - 1] = temp;
		}
    	
        // Get Dispatch Byte Arrays
        byte[] dispatchPayloadSize = getInverse4ByteArrayFromInt(
                4 + dispatchRequestHandle.length
        );
        byte[] dispatchChildCount = new byte[]{0, 1};
        byte[] dispatchCallingConvention = new byte[]{0, 0, 0, 2};

        // Get GetDWORDProperty Byte Arrays
        byte[] GetDWORDPropertyChildCount = new byte[]{0, 0};
        byte[] GetDWORDPropertyPayloadS_OK = new byte[]{0, 0, 0, 0};
        byte[] GetDWORDPropertyPayloadPropertyValue = new byte[]{0, 0, 0, 1};
        byte[] GetDWORDPropertyPayloadSize = getInverse4ByteArrayFromInt(
                GetDWORDPropertyPayloadS_OK.length +
                        GetDWORDPropertyPayloadPropertyValue.length
        );

        // Create Base Byte Array (not needed in Java, we'll use ArrayList)

        // Formulate full response using ArrayList for dynamic sizing
        ArrayList<Byte> responseList = new ArrayList<>();
        // Add Dispatch PayloadSize
        for (byte b : dispatchPayloadSize) { responseList.add(b); }
        // Add Dispatch ChildCount
        for (byte b : dispatchChildCount) { responseList.add(b); }
        // Add Dispatch CallingConvention
        for (byte b : dispatchCallingConvention) { responseList.add(b); }
        // Add Dispatch RequestHandle
        for (byte b : dispatchRequestHandle) { responseList.add(b); }
        // Add GetDWORDProperty PayloadSize
        for (byte b : GetDWORDPropertyPayloadSize) { responseList.add(b); }
        // Add GetDWORDProperty ChildCount
        for (byte b : GetDWORDPropertyChildCount) { responseList.add(b); }
        // Add GetDWORDProperty Payload Result
        for (byte b : GetDWORDPropertyPayloadS_OK) { responseList.add(b); }
        // Add GetDWORDProperty Payload PropertyValue
        for (byte b : GetDWORDPropertyPayloadPropertyValue) { responseList.add(b); }

        // Convert ArrayList<Byte> to byte[]
        byte[] response = new byte[responseList.size()];
        for (int i = 0; i < responseList.size(); i++) {
            response[i] = responseList.get(i);
        }

        // Return the created byte array
        return response;
    }
    
    public static byte[] DeviceCapabilityFalseGetDWORDPropertyResponse(int dispatchRequestHandleInt) {

    	ByteBuffer dispatchRequestHandleBuffer = ByteBuffer.allocate(4);
    	dispatchRequestHandleBuffer.order(ByteOrder.LITTLE_ENDIAN);
    	dispatchRequestHandleBuffer.putInt(dispatchRequestHandleInt);
    	byte[] dispatchRequestHandle = dispatchRequestHandleBuffer.array();
    	for (int i = 0; i < dispatchRequestHandle.length / 2; i++) {
	        byte temp = dispatchRequestHandle[i];
	        dispatchRequestHandle[i] = dispatchRequestHandle[dispatchRequestHandle.length - i - 1];
	        dispatchRequestHandle[dispatchRequestHandle.length - i - 1] = temp;
		}
    	
        // Get Dispatch Byte Arrays
        byte[] dispatchPayloadSize = getInverse4ByteArrayFromInt(
                4 + dispatchRequestHandle.length
        );
        byte[] dispatchChildCount = new byte[]{0, 1};
        byte[] dispatchCallingConvention = new byte[]{0, 0, 0, 2};

        // Get GetDWORDProperty Byte Arrays
        byte[] GetDWORDPropertyChildCount = new byte[]{0, 0};
        byte[] GetDWORDPropertyPayloadS_OK = new byte[]{0, 0, 0, 0};
        byte[] GetDWORDPropertyPayloadPropertyValue = new byte[]{0, 0, 0, 0};
        byte[] GetDWORDPropertyPayloadSize = getInverse4ByteArrayFromInt(
                GetDWORDPropertyPayloadS_OK.length +
                        GetDWORDPropertyPayloadPropertyValue.length
        );

        // Create Base Byte Array (not needed in Java, we'll use ArrayList)

        // Formulate full response using ArrayList for dynamic sizing
        ArrayList<Byte> responseList = new ArrayList<>();
        // Add Dispatch PayloadSize
        for (byte b : dispatchPayloadSize) { responseList.add(b); }
        // Add Dispatch ChildCount
        for (byte b : dispatchChildCount) { responseList.add(b); }
        // Add Dispatch CallingConvention
        for (byte b : dispatchCallingConvention) { responseList.add(b); }
        // Add Dispatch RequestHandle
        for (byte b : dispatchRequestHandle) { responseList.add(b); }
        // Add GetDWORDProperty PayloadSize
        for (byte b : GetDWORDPropertyPayloadSize) { responseList.add(b); }
        // Add GetDWORDProperty ChildCount
        for (byte b : GetDWORDPropertyChildCount) { responseList.add(b); }
        // Add GetDWORDProperty Payload Result
        for (byte b : GetDWORDPropertyPayloadS_OK) { responseList.add(b); }
        // Add GetDWORDProperty Payload PropertyValue
        for (byte b : GetDWORDPropertyPayloadPropertyValue) { responseList.add(b); }

        // Convert ArrayList<Byte> to byte[]
        byte[] response = new byte[responseList.size()];
        for (int i = 0; i < responseList.size(); i++) {
            response[i] = responseList.get(i);
        }

        // Return the created byte array
        return response;
    }
    
    public static byte[] getStringPropertyResponse(int dispatchRequestHandleInt, String propertyValueString) {

    	ByteBuffer dispatchRequestHandleBuffer = ByteBuffer.allocate(4);
    	dispatchRequestHandleBuffer.order(ByteOrder.LITTLE_ENDIAN);
    	dispatchRequestHandleBuffer.putInt(dispatchRequestHandleInt);
    	byte[] dispatchRequestHandle = dispatchRequestHandleBuffer.array();
    	for (int i = 0; i < dispatchRequestHandle.length / 2; i++) {
	        byte temp = dispatchRequestHandle[i];
	        dispatchRequestHandle[i] = dispatchRequestHandle[dispatchRequestHandle.length - i - 1];
	        dispatchRequestHandle[dispatchRequestHandle.length - i - 1] = temp;
		}
    	
        // Get Dispatch Byte Arrays
        byte[] dispatchPayloadSize = getInverse4ByteArrayFromInt(
                4 + dispatchRequestHandle.length
        );
        byte[] dispatchChildCount = new byte[]{0, 1};
        byte[] dispatchCallingConvention = new byte[]{0, 0, 0, 2};

        // Get GetStringProperty Byte Arrays
        byte[] getStringPropertyChildCount = new byte[]{0, 0};
        byte[] getStringPropertyPayloadS_OK = new byte[]{0, 0, 0, 0};
        byte[] getStringPropertyPayloadPropertyValue = (propertyValueString + "\0").getBytes(StandardCharsets.UTF_8);
        byte[] getStringPropertyPayloadLength = getInverse4ByteArrayFromInt(
                getStringPropertyPayloadPropertyValue.length
        );
        byte[] getStringPropertyPayloadSize = getInverse4ByteArrayFromInt(
                getStringPropertyPayloadS_OK.length +
                        getStringPropertyPayloadLength.length +
                        getStringPropertyPayloadPropertyValue.length  

        );

        // Formulate full response using ByteBuffer
        ByteBuffer response = ByteBuffer.allocate(
                dispatchPayloadSize.length +
                        dispatchChildCount.length +
                        dispatchCallingConvention.length +
                        dispatchRequestHandle.length +
                        getStringPropertyPayloadSize.length +
                        getStringPropertyChildCount.length +
                        getStringPropertyPayloadS_OK.length +
                        getStringPropertyPayloadLength.length +
                        getStringPropertyPayloadPropertyValue.length
        );

        response.put(dispatchPayloadSize);
        response.put(dispatchChildCount);
        response.put(dispatchCallingConvention);
        response.put(dispatchRequestHandle);
        response.put(getStringPropertyPayloadSize);
        response.put(getStringPropertyChildCount);
        response.put(getStringPropertyPayloadS_OK);
        response.put(getStringPropertyPayloadLength);
        response.put(getStringPropertyPayloadPropertyValue);

        return response.array();
    }
    
    public static byte[] GetDWORDPropertyResponse(int dispatchRequestHandleInt, int dwordPropertyValue) {

    	ByteBuffer dispatchRequestHandleBuffer = ByteBuffer.allocate(4);
    	dispatchRequestHandleBuffer.order(ByteOrder.LITTLE_ENDIAN);
    	dispatchRequestHandleBuffer.putInt(dispatchRequestHandleInt);
    	byte[] dispatchRequestHandle = dispatchRequestHandleBuffer.array();
    	for (int i = 0; i < dispatchRequestHandle.length / 2; i++) {
	        byte temp = dispatchRequestHandle[i];
	        dispatchRequestHandle[i] = dispatchRequestHandle[dispatchRequestHandle.length - i - 1];
	        dispatchRequestHandle[dispatchRequestHandle.length - i - 1] = temp;
		}
    	
        // Get Dispatch Byte Arrays
        byte[] dispatchPayloadSize = getInverse4ByteArrayFromInt(
                4 + dispatchRequestHandle.length
        );
        byte[] dispatchChildCount = new byte[]{0, 1};
        byte[] dispatchCallingConvention = new byte[]{0, 0, 0, 2};

        // Get GetDWORDProperty Byte Arrays
        byte[] GetDWORDPropertyChildCount = new byte[]{0, 0};
        byte[] GetDWORDPropertyPayloadS_OK = new byte[]{0, 0, 0, 0};
        byte[] GetDWORDPropertyPayloadPropertyValue = getInverse4ByteArrayFromInt(dwordPropertyValue);
        byte[] GetDWORDPropertyPayloadSize = getInverse4ByteArrayFromInt(
                GetDWORDPropertyPayloadS_OK.length +
                        GetDWORDPropertyPayloadPropertyValue.length
        );

        // Create Base Byte Array (not needed in Java, we'll use ArrayList)

        // Formulate full response using ArrayList for dynamic sizing
        ArrayList<Byte> responseList = new ArrayList<>();
        // Add Dispatch PayloadSize
        for (byte b : dispatchPayloadSize) { responseList.add(b); }
        // Add Dispatch ChildCount
        for (byte b : dispatchChildCount) { responseList.add(b); }
        // Add Dispatch CallingConvention
        for (byte b : dispatchCallingConvention) { responseList.add(b); }
        // Add Dispatch RequestHandle
        for (byte b : dispatchRequestHandle) { responseList.add(b); }
        // Add GetDWORDProperty PayloadSize
        for (byte b : GetDWORDPropertyPayloadSize) { responseList.add(b); }
        // Add GetDWORDProperty ChildCount
        for (byte b : GetDWORDPropertyChildCount) { responseList.add(b); }
        // Add GetDWORDProperty Payload Result
        for (byte b : GetDWORDPropertyPayloadS_OK) { responseList.add(b); }
        // Add GetDWORDProperty Payload PropertyValue
        for (byte b : GetDWORDPropertyPayloadPropertyValue) { responseList.add(b); }

        // Convert ArrayList<Byte> to byte[]
        byte[] response = new byte[responseList.size()];
        for (int i = 0; i < responseList.size(); i++) {
            response[i] = responseList.get(i);
        }

        // Return the created byte array
        return response;
    }
    
	//endregion ###############################################################

    
	//region DSMN Functions ###################################################
    
    public static byte[] getQWaveSinkInfoResponse(int dispatchRequestHandleInt) {

    	ByteBuffer dispatchRequestHandleBuffer = ByteBuffer.allocate(4);
    	dispatchRequestHandleBuffer.order(ByteOrder.LITTLE_ENDIAN);
    	dispatchRequestHandleBuffer.putInt(dispatchRequestHandleInt);
    	byte[] dispatchRequestHandle = dispatchRequestHandleBuffer.array();
    	for (int i = 0; i < dispatchRequestHandle.length / 2; i++) {
	        byte temp = dispatchRequestHandle[i];
	        dispatchRequestHandle[i] = dispatchRequestHandle[dispatchRequestHandle.length - i - 1];
	        dispatchRequestHandle[dispatchRequestHandle.length - i - 1] = temp;
		}
    	
        // Get Dispatch Byte Arrays
        byte[] dispatchPayloadSize = getInverse4ByteArrayFromInt(
                4 + dispatchRequestHandle.length
        );
        byte[] dispatchChildCount = new byte[]{0, 1};
        byte[] dispatchCallingConvention = new byte[]{0, 0, 0, 2};

        // Get GetQWaveSinkInfo Byte Arrays
        byte[] getQWaveSinkInfoChildCount = new byte[]{0, 0};
        byte[] getQWaveSinkInfoPayloadS_OK = new byte[]{0, 0, 0, 0};
        byte[] getQWaveSinkInfoPayloadIsSinkRunning = new byte[]{0, 0, 0, 0};
        byte[] getQWaveSinkInfoPayloadPortNumber = getInverse4ByteArrayFromInt(2177);
        byte[] getQWaveSinkInfoPropertyPayloadSize = getInverse4ByteArrayFromInt(
                getQWaveSinkInfoPayloadS_OK.length +
                        getQWaveSinkInfoPayloadIsSinkRunning.length +
                        getQWaveSinkInfoPayloadPortNumber.length
        );

        // Formulate full response using ByteBuffer
        ByteBuffer response = ByteBuffer.allocate(
                dispatchPayloadSize.length +
                        dispatchChildCount.length +
                        dispatchCallingConvention.length +
                        dispatchRequestHandle.length +
                        getQWaveSinkInfoPropertyPayloadSize.length +
                        getQWaveSinkInfoChildCount.length +
                        getQWaveSinkInfoPayloadS_OK.length +
                        getQWaveSinkInfoPayloadIsSinkRunning.length +
                        getQWaveSinkInfoPayloadPortNumber.length
        );

        response.put(dispatchPayloadSize);
        response.put(dispatchChildCount);
        response.put(dispatchCallingConvention);
        response.put(dispatchRequestHandle);
        response.put(getQWaveSinkInfoPropertyPayloadSize);
        response.put(getQWaveSinkInfoChildCount);
        response.put(getQWaveSinkInfoPayloadS_OK);
        response.put(getQWaveSinkInfoPayloadIsSinkRunning);
        response.put(getQWaveSinkInfoPayloadPortNumber);

        return response.array();
    }
    
	//endregion ###############################################################

    
	//region DRMRI Functions ##################################################
    
    public static byte[] registerTransmitterServiceResponse(int dispatchRequestHandleInt) {

    	ByteBuffer dispatchRequestHandleBuffer = ByteBuffer.allocate(4);
    	dispatchRequestHandleBuffer.order(ByteOrder.LITTLE_ENDIAN);
    	dispatchRequestHandleBuffer.putInt(dispatchRequestHandleInt);
    	byte[] dispatchRequestHandle = dispatchRequestHandleBuffer.array();
    	for (int i = 0; i < dispatchRequestHandle.length / 2; i++) {
	        byte temp = dispatchRequestHandle[i];
	        dispatchRequestHandle[i] = dispatchRequestHandle[dispatchRequestHandle.length - i - 1];
	        dispatchRequestHandle[dispatchRequestHandle.length - i - 1] = temp;
		}
    	
        // Get Dispatch Byte Arrays
        byte[] dispatchPayloadSize = getInverse4ByteArrayFromInt(
                4 + dispatchRequestHandle.length
        );
        byte[] dispatchChildCount = new byte[]{0, 1};
        byte[] dispatchCallingConvention = new byte[]{0, 0, 0, 2};

        // Get GetDWORDProperty Byte Arrays
        byte[] GetDWORDPropertyChildCount = new byte[]{0, 0};
        byte[] RegisterTransmitterServicePayloadDSLR_E_FAIL = new byte[]{(byte) 136, 23, 64, 5};
        byte[] GetDWORDPropertyPayloadSize = getInverse4ByteArrayFromInt(
        		RegisterTransmitterServicePayloadDSLR_E_FAIL.length
        );

        // Create Base Byte Array (not needed in Java, we'll use ArrayList)

        // Formulate full response using ArrayList for dynamic sizing
        ArrayList<Byte> responseList = new ArrayList<>();
        // Add Dispatch PayloadSize
        for (byte b : dispatchPayloadSize) { responseList.add(b); }
        // Add Dispatch ChildCount
        for (byte b : dispatchChildCount) { responseList.add(b); }
        // Add Dispatch CallingConvention
        for (byte b : dispatchCallingConvention) { responseList.add(b); }
        // Add Dispatch RequestHandle
        for (byte b : dispatchRequestHandle) { responseList.add(b); }
        // Add GetDWORDProperty PayloadSize
        for (byte b : GetDWORDPropertyPayloadSize) { responseList.add(b); }
        // Add GetDWORDProperty ChildCount
        for (byte b : GetDWORDPropertyChildCount) { responseList.add(b); }
        // Add GetDWORDProperty Payload Result
        for (byte b : RegisterTransmitterServicePayloadDSLR_E_FAIL) { responseList.add(b); }

        // Convert ArrayList<Byte> to byte[]
        byte[] response = new byte[responseList.size()];
        for (int i = 0; i < responseList.size(); i++) {
            response[i] = responseList.get(i);
        }

        // Return the created byte array
        return response;
    }
    
	//endregion ###############################################################

    
    public static byte[] GenericOKResponse(int dispatchRequestHandleInt) {

    	ByteBuffer dispatchRequestHandleBuffer = ByteBuffer.allocate(4);
    	dispatchRequestHandleBuffer.order(ByteOrder.LITTLE_ENDIAN);
    	dispatchRequestHandleBuffer.putInt(dispatchRequestHandleInt);
    	byte[] dispatchRequestHandle = dispatchRequestHandleBuffer.array();
    	for (int i = 0; i < dispatchRequestHandle.length / 2; i++) {
	        byte temp = dispatchRequestHandle[i];
	        dispatchRequestHandle[i] = dispatchRequestHandle[dispatchRequestHandle.length - i - 1];
	        dispatchRequestHandle[dispatchRequestHandle.length - i - 1] = temp;
		}
    	
        // Get Dispatch Byte Arrays
        byte[] dispatchPayloadSize = getInverse4ByteArrayFromInt(
                4 + dispatchRequestHandle.length
        );
        byte[] dispatchChildCount = new byte[]{0, 1};
        byte[] dispatchCallingConvention = new byte[]{0, 0, 0, 2};

        // Get GetDWORDProperty Byte Arrays
        byte[] GetDWORDPropertyChildCount = new byte[]{0, 0};
        byte[] GetDWORDPropertyPayloadS_OK = new byte[]{0, 0, 0, 0};
        byte[] GetDWORDPropertyPayloadSize = getInverse4ByteArrayFromInt(
                GetDWORDPropertyPayloadS_OK.length
        );

        // Create Base Byte Array (not needed in Java, we'll use ArrayList)

        // Formulate full response using ArrayList for dynamic sizing
        ArrayList<Byte> responseList = new ArrayList<>();
        // Add Dispatch PayloadSize
        for (byte b : dispatchPayloadSize) { responseList.add(b); }
        // Add Dispatch ChildCount
        for (byte b : dispatchChildCount) { responseList.add(b); }
        // Add Dispatch CallingConvention
        for (byte b : dispatchCallingConvention) { responseList.add(b); }
        // Add Dispatch RequestHandle
        for (byte b : dispatchRequestHandle) { responseList.add(b); }
        // Add GetDWORDProperty PayloadSize
        for (byte b : GetDWORDPropertyPayloadSize) { responseList.add(b); }
        // Add GetDWORDProperty ChildCount
        for (byte b : GetDWORDPropertyChildCount) { responseList.add(b); }
        // Add GetDWORDProperty Payload Result
        for (byte b : GetDWORDPropertyPayloadS_OK) { responseList.add(b); }

        // Convert ArrayList<Byte> to byte[]
        byte[] response = new byte[responseList.size()];
        for (int i = 0; i < responseList.size(); i++) {
            response[i] = responseList.get(i);
        }

        // Return the created byte array
        return response;
    }
    

    private static byte[] getInverse4ByteArrayFromInt(int intValue) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(intValue);
        byte[] intBytes = buffer.array();
        
//        for (int i = 0; i < intBytes.length / 2; i++) {
//            byte temp = intBytes[i];
//            intBytes[i] = intBytes[intBytes.length - i - 1];
//            intBytes[intBytes.length - i - 1] = temp;
//        }
        return intBytes;
    }

    public static byte[] encapsulate(byte[] byteArray) {
        byte[] segment1 = getByteArrayFromInt(byteArray.length);
        byte[] segment2 = new byte[]{19, 0, 0, 0};
        // Formulate encapsulated array using ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(
                segment1.length + segment2.length + byteArray.length
        );
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.put(segment1);
        buffer.put(segment2);
        buffer.put(byteArray);
        return buffer.array();
    }
    
    private static byte[] getByteArrayFromInt(int intValue) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(intValue);
        return buffer.array();
    }
    
    private static byte[] getInverse8ByteArrayFromLong(long longValue) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(longValue);
        byte[] longBytes = buffer.array();

        // Reverse the array if the system is little-endian
        if (java.nio.ByteOrder.nativeOrder() == java.nio.ByteOrder.LITTLE_ENDIAN) {
            for (int i = 0; i < longBytes.length / 2; i++) {
                byte temp = longBytes[i];
                longBytes[i] = longBytes[longBytes.length - i - 1];
                longBytes[longBytes.length - i - 1] = temp;
            }
        }
        return longBytes;
    }
}

