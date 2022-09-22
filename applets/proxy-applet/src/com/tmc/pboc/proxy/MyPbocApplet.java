package com.tmc.pboc.proxy;


import com.tmc.pboc.shareable.PbocShareable;


import javacard.framework.AID;
import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.Shareable;

import javacard.framework.Util;

public class MyPbocApplet extends Applet implements PbocShareable{

	// A0 00 00 00 05 38 38 00 01 02
		// proxy applet实例aid
		private byte[] proxyAppAid = new byte[]	{(byte)0xA0, (byte)0x00,
	            (byte)0x00, (byte)0x00, (byte)0x05, 
	            (byte)0x38, (byte)0x38, (byte)0x00, (byte)0x01, (byte)0x02
	           }; 
		public static void install(byte[] bArray, short bOffset, byte bLength) {
			// GP-compliant JavaCard applet registration
			new MyPbocApplet(bArray,bOffset,bLength).register(bArray, (short) (bOffset + 1), bArray[bOffset]);
		}

		
		private  byte[]  offsetBArryData = null;
		
		// 安装参数,不包括长度
		private byte[] installParametersData = null;
		// aid,不包括长度
		private byte[] aidData = null;
		
		private MyPbocApplet(){
			
		}
		
		private MyPbocApplet(byte[] bArray, short bOffset, byte bLength){
			short ParaOffset = bOffset;
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
			


			// 拷贝偏移数据
			offsetBArryData = new byte[bLength];
			Util.arrayCopyNonAtomic(bArray, (short)bOffset, offsetBArryData, (short)0, (short)bLength);

		}
		
		public void process(APDU apdu) {
			// Good practice: Return 9000 on SELECT
			if (selectingApplet()) {
				return;
			}

			byte[] buf = apdu.getBuffer();
			
			switch (buf[ISO7816.OFFSET_INS]) {
				case (byte) 0x00:
					break;
				default:
					// good practice: If you don't know the INStruction, say so:
					ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
			}
		}
		
		private byte[] errorResult = new byte[]{ (byte)(0x00),(byte)(0x00)};
		
		// 返回实例对应银行卡( pboc ) AID + 指令中的 Data
		public short transmitAPDU(byte[] buf, byte[] outCommand) {
			// 从 proxy applet传递过来的 aid 应该等于 对应的实例 AID
			
			byte compareResult = Util.arrayCompare(aidData, (short)0, buf, (short)6, (short)aidData.length);
			if( compareResult != 0){
				errorResult[1] = 1;
				Util.arrayCopyNonAtomic(errorResult, (short)0, buf, (short)0, (short)2);
				return 2;
			}
			 short aidlen = 	buf[5];
			 byte[] aid = new byte[aidlen];
			Util.arrayCopyNonAtomic(buf, (short)6, aid, (short)0, (short)aidlen);
			Util.arrayCopyNonAtomic(aid, (short)0, buf, (short)0, (short)aidlen);
			return (short)aidlen;
			//return 3;
		}
		
		 public Shareable getShareableInterfaceObject(AID clientAID, byte parameter){
			 
			 //  要求 parameter 必须为 1
			/* if( parameter != 1 ){
				  return null;
			  }
			 // clientAID 必须为 crs aid ( proxy applet aid)
			boolean aidMatch =  clientAID.equals(proxyAppAid, (short)0, (byte)proxyAppAid.length);
			
			if( aidMatch){
				return null;
			}*/
		      return this;
		 }

		public void send() {
			// TODO Auto-generated method stub
			
		} 

}
