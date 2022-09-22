

package com.tmc.pboc.proxy;

import com.tmc.pboc.shareable.PbocShareable;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.JCSystem;
import javacard.framework.Util;

public class ProxyApplet extends Applet {
	
	private final static byte IN_APP_INS = (byte)0x01;
	
	private final static byte INSTALL_PARAM_INS1 = (byte)0x06;
	private final static byte INSTALL_PARAM_INS2 = (byte)0x02;
	private final static byte INSTALL_PARAM_INS3 = (byte)0x03;
	private final static byte INSTALL_PARAM_INS4 = (byte)0x04;
	private final static byte INSTALL_PARAM_INS5 = (byte)0x05;

	private  byte[]  allBArryData = null;
	private  byte[]  offsetBArryData = null;
	
	// 安装参数,不包括长度
	private byte[] installParametersData = null;
	// aid,不包括长度
	private byte[] aidData = null;
	
/*	安装实例:
		apdu 80E60C00 28 08 A000000005383800 09 A00000000538380001 0A A0000000053838000102 0100 06 C9 0411223344  00
		apdu 80E60C002808A00000000538380009A000000005383800010AA0000000053838000102010006C9041122334400

		获取到的数据:
		0A A0000000053838000102 0100 0411223344
*/
	
	// 拿到 Pobc applet共享对象
	private PbocShareable pbocShareableObj;
	
	private ProxyApplet(){
		
	}
	private ProxyApplet(byte[] bArray, short bOffset, byte bLength){
	/*	short ParaOffset = bOffset;
		short aidLen = bArray[ParaOffset];
		
		// 拷贝aid到
		aidData = new byte[aidLen];
		Util.arrayCopyNonAtomic(bArray, (short)(ParaOffset+1), aidData, (short)0, (short)aidLen);
		
		//计算安装参数偏移	
		//AID
		ParaOffset += (short)(1+(short)bArray[ParaOffset]);
		//应用权限
		ParaOffset += (short)(1+(short)bArray[ParaOffset]);
		
		// C9参数的长度 
		short c9ParamLen = (short)bArray[ParaOffset];
		installParametersData = new byte[c9ParamLen];
		Util.arrayCopyNonAtomic(bArray, (short)(ParaOffset+1), installParametersData, (short)0, (short)c9ParamLen);
		
		short length = (short)bArray.length;
		allBArryData = new byte[length];
		Util.arrayCopyNonAtomic(bArray, (short)0, allBArryData, (short)0, (short)bArray.length);

		// 拷贝偏移数据
		offsetBArryData = new byte[bLength];
		Util.arrayCopyNonAtomic(bArray, (short)bOffset, offsetBArryData, (short)0, (short)bLength);*/

	}
	
	public static void install(byte[] bArray, short bOffset, byte bLength) {
		// GP-compliant JavaCard applet registration
		new ProxyApplet().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
	}

	public void process(APDU apdu) {
		byte[] apduBuffer = apdu.getBuffer();
		byte cla = apduBuffer[ISO7816.OFFSET_CLA];
		byte ins = apduBuffer[ISO7816.OFFSET_INS];
		
		// Good practice: Return 9000 on SELECT
		if (selectingApplet()) {
			
		
			return;
		}

		switch ( ins ) {
			case IN_APP_INS:
				//  GET ONLINE TRANSACTION DATA
				getOnlineTransactionData(apdu,apduBuffer);
				break;
			case (byte) 0x00:
				break;
			case INSTALL_PARAM_INS1:
				getAllData(apdu,apduBuffer);
				break;
			case INSTALL_PARAM_INS2:
				getOffsetData(apdu,apduBuffer);
				break;
			case INSTALL_PARAM_INS3:
				getAidData(apdu,apduBuffer);
				break;
			case INSTALL_PARAM_INS4:
				getC9InstallParamData(apdu,apduBuffer);
				break;
			default:
				// good practice: If you don't know the INStruction, say so:
				ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
		
		
	}
	

	private void getAllData(APDU apdu, byte[] apduBuffer) {
		Util.arrayCopyNonAtomic(allBArryData, (short)0, apduBuffer, (short)0, (short)allBArryData.length);
		apdu.setOutgoingAndSend((short) 0, (short) allBArryData.length);
	}
	private void getOffsetData(APDU apdu, byte[] apduBuffer) {
		Util.arrayCopyNonAtomic(offsetBArryData, (short)0, apduBuffer, (short)0, (short)offsetBArryData.length);
		apdu.setOutgoingAndSend((short) 0, (short)offsetBArryData.length);
	}

	private void getAidData(APDU apdu, byte[] apduBuffer) {
		Util.arrayCopyNonAtomic(aidData, (short)0, apduBuffer, (short)0, (short)aidData.length);
		apdu.setOutgoingAndSend((short) 0, (short)aidData.length);
	}
	private void getC9InstallParamData(APDU apdu, byte[] apduBuffer) {
		Util.arrayCopyNonAtomic(installParametersData, (short)0, apduBuffer, (short)0, (short)installParametersData.length);
		apdu.setOutgoingAndSend((short) 0, (short)installParametersData.length);
	}
	
	private void getOnlineTransactionData(APDU apdu, byte[] apduBuffer) {
		// byte[] pbocServerAid = new byte[10];
		// 拷贝 aid
		// Util.arrayCopyNonAtomic(apduBuffer, (short)0, apduBuffer, (short)0, (short)installParametersData.length);

		apdu.setIncomingAndReceive();

		// 从 apdu buffer中获取 AID
		pbocShareableObj = (PbocShareable)JCSystem.getAppletShareableInterfaceObject(
				JCSystem.lookupAID(apduBuffer,(short)6,(byte)10), (byte)1);
		
		/* byte[] serverAppAid = new byte[]	{(byte)0xA0, (byte)0x00,
	            (byte)0x00, (byte)0x00, (byte)0x05, 
	            (byte)0x38, (byte)0x39, (byte)0x00, (byte)0x01, (byte)0x02};
		 
		 pbocShareableObj = (PbocShareable)JCSystem.getAppletShareableInterfaceObject(
				JCSystem.lookupAID(serverAppAid,(short)0,(byte)10), (byte)1);
		*/
		
		
		 if ( pbocShareableObj == null ){
			 ISOException.throwIt((short)ISO7816.SW_WRONG_DATA); 
			 // no server applet  
		 }
		// ISOException.throwIt((short)ISO7816.SW_INS_NOT_SUPPORTED);
		 
		short len = pbocShareableObj.transmitAPDU(apduBuffer, apduBuffer);
		 
		 
		 
		// pbocShareableObj.send();
		
		// Util.arrayCopyNonAtomic(installParametersData, (short)0, apduBuffer, (short)0, (short)installParametersData.length);
		
		apdu.setOutgoingAndSend((short) 0, (short)len);
	}
	
}
