/**
 *
 * COPYRIGHT (C) 2010, 2011, 2012, 2013, 2014 AGETO Innovation GmbH
 *
 * Authors Christian Kahlo, Ralf Wondratschek
 *
 * All Rights Reserved.
 *
 * Contact: PersoApp, http://www.persoapp.de
 *
 * @version 1.0, 30.07.2013 13:50:47
 *
 *          This file is part of PersoApp.
 *
 *          PersoApp is free software: you can redistribute it and/or modify it
 *          under the terms of the GNU Lesser General Public License as
 *          published by the Free Software Foundation, either version 3 of the
 *          License, or (at your option) any later version.
 *
 *          PersoApp is distributed in the hope that it will be useful, but
 *          WITHOUT ANY WARRANTY; without even the implied warranty of
 *          MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *          Lesser General Public License for more details.
 *
 *          You should have received a copy of the GNU Lesser General Public
 *          License along with PersoApp. If not, see
 *          <http://www.gnu.org/licenses/>.
 *
 *          Diese Datei ist Teil von PersoApp.
 *
 *          PersoApp ist Freie Software: Sie können es unter den Bedingungen der
 *          GNU Lesser General Public License, wie von der Free Software
 *          Foundation, Version 3 der Lizenz oder (nach Ihrer Option) jeder
 *          späteren veröffentlichten Version, weiterverbreiten und/oder
 *          modifizieren.
 *
 *          PersoApp wird in der Hoffnung, dass es nützlich sein wird, aber OHNE
 *          JEDE GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite
 *          Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN
 *          ZWECK. Siehe die GNU Lesser General Public License für weitere
 *          Details.
 *
 *          Sie sollten eine Kopie der GNU Lesser General Public License
 *          zusammen mit diesem Programm erhalten haben. Wenn nicht, siehe
 *          <http://www.gnu.org/licenses/>.
 *
 */
package de.persoapp.core.tls;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;

import javax.net.ssl.SSLSocketFactory;

/*
 * pre-liminary stub
 */
/**
 * The BCTlsSocketFactory works like an regular SSLSocketFactory but stores also
 * an additional pre shared key and the eID-SessionID.
 * 
 * @author Christian Kahlo
 * @author Rico Klimsa - added javadoc comments.
 */
public final class BCTlsSocketFactoryImpl extends SSLSocketFactory {

	/*
	 * arbitrary value, trying to be reasonable
	 */
	/**
	 * Stops the attempt to connect the server, after the timeout runs out.
	 */
	private static final int	SO_CONNECT_TIMEOUT	= 10 * 1000;	// 10 seconds

	/**
	 * eID-SessiionID
	 */
	final byte[]				pskId;
	
	/**
	 * The currently used pre-shared-key.
	 */
	final byte[]				pskKey;

	/**
	 * Creates a new {@link BCTlsSocketFactoryImpl}.
	 */
	public BCTlsSocketFactoryImpl() {
		pskId = null;
		pskKey = null;
	}

	/**
	 * Creates and initializes an new {@link BCTlsSocketFactoryImpl}.
	 * 
	 * @param pskId
	 *            - The id of the pre-shared key.
	 * @param pskKey
	 *            - The pre-shared key itself.
	 */
	public BCTlsSocketFactoryImpl(final byte[] pskId, final byte[] pskKey) {
		this.pskId = pskId;
		this.pskKey = pskKey;
	}

	/**
	 * Returns the parameters of the pre-shared key. These are in detail:
	 * <p>
	 * <ul>
	 * <li>The id of the pre-shared key.</li>
	 * <li>The pre-shared key itself.</li>
	 * </ul>
	 * </p>
	 * If no parameters are set, <strong>null</strong> is returned.
	 * 
	 * @return Returns the psk-parameters in a 2-elements sized byte array with
	 *         the pskId on the first place and the pskKey on
	 *         the second place of the returned array. If no parameters are set,
	 *         <strong>null</strong> is returned.
	 */
	final byte[][] getPSKParameters() {
		if (pskId != null && pskKey != null) {
			return new byte[][] { pskId, pskKey };
		}

		return null;
	}

	@Override
	public Socket createSocket(Socket socket, final String host, final int port, final boolean autoClose)
			throws IOException {
		if (socket == null) {
			/*
			 * create channel-based socket to allow NIO usage later
			 */
			socket = SocketChannel.open().socket();
		}

		if (!socket.isBound()) {
			socket.bind(null);
		}

		if (!socket.isConnected()) {
			socket.connect(new InetSocketAddress(host, port), SO_CONNECT_TIMEOUT);
		}

		return new BCTlsSocketImpl(socket, this, false);
	}

	@Override
	public String[] getDefaultCipherSuites() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getSupportedCipherSuites() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Socket createSocket(final String host, final int port) throws IOException, UnknownHostException {
		return createSocket(null, host, port, true);

	}

	@Override
	public Socket createSocket(final InetAddress host, final int port) throws IOException {
		return createSocket(null, host.getCanonicalHostName(), port, true);
	}

	// not used, don't implement for now

	@Override
	public Socket createSocket(final String host, final int port, final InetAddress localhost, final int localport)
			throws IOException, UnknownHostException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Socket createSocket(final InetAddress host, final int port, final InetAddress localhost, final int localport)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}
