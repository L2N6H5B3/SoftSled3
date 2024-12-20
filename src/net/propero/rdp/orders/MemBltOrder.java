/* MemBltOrder.java
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

public class MemBltOrder extends ScreenBltOrder {

	private int color_table = 0;

	private int cache_id = 0;

	private int cache_idx = 0;

	public MemBltOrder() {
		super();
	}

	public int getColorTable() {
		return this.color_table;
	}

	public int getCacheID() {
		return this.cache_id;
	}

	public int getCacheIDX() {
		return this.cache_idx;
	}

	public void setColorTable(int color_table) {
		this.color_table = color_table;
	}

	public void setCacheID(int cache_id) {
		this.cache_id = cache_id;
	}

	public void setCacheIDX(int cache_idx) {
		this.cache_idx = cache_idx;
	}

	public void reset() {
		super.reset();
		color_table = 0;
		cache_id = 0;
		cache_idx = 0;
	}
}
