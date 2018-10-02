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
package org.infoglue.deliver.taglib.common;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;

import org.infoglue.cms.controllers.kernel.impl.simple.PublicationController;
import org.infoglue.cms.entities.publishing.PublicationVO;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.deliver.taglib.AbstractTag;

public class EntityPublicationTag extends AbstractTag 
{
	private static final long serialVersionUID = 8603406098980150888L;
	
	private String entityName;
	private String entityId;
	
	public EntityPublicationTag() 
	{
		super();
	}
	
	public int doEndTag() throws JspException
    {
		List<PublicationVO> publicationList;
		try {
			publicationList = PublicationController.getController().getPublicationListByEntityValues(entityName, entityId);
		
			if (publicationList != null && publicationList.size() > 0) {
				setResultAttribute(publicationList.get(0));
			}
			this.entityName = null;
			this.entityId = null;
		} catch (SystemException e) {
			throw new JspException("Error getting publication list for entityName:" + entityName + ", entityId:" + entityId);
		}
        return EVAL_PAGE;
    }
    public void setEntityName(final String entityName) throws JspException
    {
        this.entityName = evaluateString("entityName", "String", entityName);
    }
    
    public void setEntityId(final String entityId) throws JspException
    {
        this.entityId = evaluateString("entitityId", "String", entityId);
    }
}
