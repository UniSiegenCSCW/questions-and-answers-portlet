package org.sidate.qanda.portlet;

import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import org.osgi.service.component.annotations.Component;

import javax.swing.text.html.HTMLDocument;
import java.util.Iterator;
import java.util.Map;

@Component(
        property = {
                "editor.config.key=qandaEditor",
                "editor.name=ckeditor",
                "service.ranking:Integer=100"

        },

        service = EditorConfigContributor.class
)
public class QandAEditorConfigContributor extends BaseEditorConfigContributor {
    @Override
    public void populateConfigJSONObject(JSONObject jsonObject, Map<String, Object> inputEditorTaglibAttributes, ThemeDisplay themeDisplay, RequestBackedPortletURLFactory requestBackedPortletURLFactory) {


        JSONArray toolbarLiferayArticle = jsonObject.getJSONArray("toolbar_liferayArticle");

        JSONArray toolbarLiferayArticleNew = JSONFactoryUtil.createJSONArray();


        if(toolbarLiferayArticle!=null){

            for(int i = 0; i < toolbarLiferayArticle.length(); i++){
                if(i!=11){
                    toolbarLiferayArticleNew.put(toolbarLiferayArticle.get(i));
                }
            }

            jsonObject.remove("toolbar_liferayArticle");
            jsonObject.put("toolbar_liferayArticle", toolbarLiferayArticleNew);
        }


        /*JSONObject toolbars = jsonObject.getJSONObject("toolbars");


        if (toolbars != null) {
            JSONObject toolbarAdd = toolbars.getJSONObject("add");

            if (toolbarAdd != null) {
                JSONArray addButtons = toolbarAdd.getJSONArray("buttons");

                addButtons.put("camera");
            }
        }*/
    }
}
