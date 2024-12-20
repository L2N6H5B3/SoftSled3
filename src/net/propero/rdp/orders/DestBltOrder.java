/* DestBltOrder.java
 * Component: ProperJavaRDP
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
package net.propero.rdp.orders;

public class DestBltOrder implements Order {

	private int x = 0;

	private int y = 0;

	private int cx = 0;

	private int cy = 0;

	private int opcode = 0;

	public DestBltOrder() {
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getCX() {
		return this.cx;
	}

	public int getCY() {
		return this.cy;
	}

	public int getOpcode() {
		return this.opcode;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setCX(int cx) {
		this.cx = cx;
	}

	public void setCY(int cy) {
		this.cy = cy;
	}

	public void setOpcode(int opcode) {
		this.opcode = opcode;
	}

	public void reset() {
		x = 0;
		y = 0;
		cx = 0;
		cy = 0;
		opcode = 0;
	}
}
