/* ===============================================================================
*
* Part of the InfoGlue Content Management Platform (www.infoglue.org)
*
* ===============================================================================
*
*  Copyright (C)
* 
* This program is free software; you can redistribute it and/or modify it under
* the terms of the GNU General Public License version 2, as published by the
* Free Software Foundation. See the file LICENSE.html for more information.
* 
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY, including the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License along with
* this program; if not, write to the Free Software Foundation, Inc. / 59 Temple
* Place, Suite 330 / Boston, MA 02111-1307 / USA.
*
* ===============================================================================
*/

package org.infoglue.cms.applications.contenttool.actions;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentVersionController;
import org.infoglue.cms.controllers.kernel.impl.simple.DigitalAssetController;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.entities.content.SmallestContentVersionVO;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.deliver.controllers.kernel.impl.simple.TemplateController;

/**
* This action downloads an asset from the system.
* 
* @author Mattias Bogeblad
*/

public class DownloadAssetAction extends InfoGlueAbstractAction
{
    private final static Logger logger = Logger.getLogger(DownloadAssetAction.class.getName());

	private static final long serialVersionUID = 1L;
	
	private Integer contentId;
	private Integer languageId;
	private String assetKey;
	private Integer assetId;
	
	protected String doExecute() throws Exception 
	{
		String assetUrl = null;
		if (contentId != null && assetKey != null && languageId != null) {
			try
			{
				assetUrl = DigitalAssetController.getDigitalAssetUrl(contentId, languageId, assetKey, true);
			}
			catch(Exception e)
			{
				logger.info("Could not download asset on contentId:" + contentId + " (" + languageId + "/" + assetKey + ")");
			}
		} else if (assetId != null) {
			try 
			{
				// Check if this asset belongs to the latest active content version
				boolean assetAvailable = false;
				List<SmallestContentVersionVO> contentVersions = DigitalAssetController.getContentVersionVOListConnectedToAssetWithId(assetId);
				int operatingMode = new Integer(CmsPropertyHandler.getOperatingMode());
						
				for (SmallestContentVersionVO currentVersion : contentVersions) {
					// Get the latest active content version that matches both the current version's contentId and its languageId
					ContentVersionVO latestActiveVersion = ContentVersionController.getContentVersionController().getLatestActiveContentVersionVO(currentVersion.getContentId(), currentVersion.getLanguageId());
					// Check if the current version matches the latest active version and if it is visible in the current operating mode
					if (currentVersion.getId().equals(latestActiveVersion.getId()) && currentVersion.getStateId() >= operatingMode) {
						assetAvailable = true;
					}
				}
				
				// Only create url to asset if it belongs to the latest active content version, otherwise it would be possible to access unpublished assets.
				if (assetAvailable) {
					assetUrl = DigitalAssetController.getDigitalAssetUrl(assetId, false);
				} else {
					logger.info("Asset not available in the latest active content version. AssetId:" + assetId);
				}
			}
			catch(Exception e)
			{
				logger.info("Could not download asset on assetId:" + assetId, e);
			}
		}

		if (assetUrl != null)
		{
			this.getResponse().sendRedirect(assetUrl);
		} 
		else
		{
			logger.info("Could not find asset since parameters were not set correctly.");
			logger.debug("contentId: " + contentId + ", languageId: " + languageId + ", assetKey: " + assetKey + ", assetId: " + assetId);
			this.getResponse().sendError(HttpServletResponse.SC_NOT_FOUND);
		}
		
		return NONE;
	}

	public String getAssetKey() 
	{
		return assetKey;
	}
	
	public void setAssetKey(String assetKey) 
	{
		this.assetKey = assetKey;
	}
	
	public Integer getContentId() 
	{
		return contentId;
	}
	
	public void setContentId(Integer contentId) 
	{
		this.contentId = contentId;
	}

	public void setAssetId(Integer assetId) 
	{
		this.assetId = assetId;
	}
	
	public Integer getLanguageId() 
	{
		return languageId;
	}
	
	public void setLanguageId(Integer languageId) 
	{
		this.languageId = languageId;
	}
}
