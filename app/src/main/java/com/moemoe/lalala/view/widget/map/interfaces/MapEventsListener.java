/*************************************************************************
* Copyright (c) 2015 Lemberg Solutions
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
**************************************************************************/

package com.moemoe.lalala.view.widget.map.interfaces;

public interface MapEventsListener 
{
    /**
     * Is called before zoom in.
     */
	public void onPreZoomIn();
	
	/**
	 * Is called when zoom in finished.
	 */
	public void onPostZoomIn();
	
	/**
	 * Is called before zoom out.
	 */
	public void onPreZoomOut();
	
	/**
	 * Is called when zoom out is finished.
	 */
	public void onPostZoomOut();
}
