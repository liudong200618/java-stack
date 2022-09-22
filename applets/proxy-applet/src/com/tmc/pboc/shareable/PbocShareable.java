package com.tmc.pboc.shareable;

import javacard.framework.Shareable;

public interface PbocShareable extends Shareable {
	/**
	 * 供proxy applet 访问
	 * @param inCommand	receive APDU Byte
	 * @param outCommand	send APDU Byte
	 * @return 传出数据的长度
	 */
	public short transmitAPDU(byte[] inCommand, byte [] outCommand);
	public void send();

}
