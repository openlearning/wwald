package org.wwald.view;

import java.io.Serializable;
import java.sql.Connection;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.FeedbackMessages;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.wwald.WWALDApplication;
import org.wwald.WicketIdConstants;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Permission;
import org.wwald.service.DataException;


public class KVTablePage extends AccessControlledPage {
	private String kvKey;
	private String kvValue = "";
	private String labelMessage = "Please enter the value for '%s' below";
	
	private transient Logger cLogger = Logger.getLogger(KVTablePage.class);
	
	public KVTablePage(PageParameters parameters) {
		super(parameters);
		try {
			this.kvKey = parameters.getString(WicketIdConstants.KVTableKey);
			add(new Label(WicketIdConstants.KVTablePage_LABEL, String.format(this.labelMessage, this.kvKey)));
			add(new Label("feedback_message",new Model<String>() {
				public String getObject() {
					return getWWALDFeedbackMessageAsString();
				}
			}));
			populateKvValue();
			add(getForm());
		} catch(DataException de) {
			String msg = "Could not get the kvtableclob value - ";
			cLogger.error(msg, de);
			setResponsePage(GenericErrorPage.class);
		}
	}

	private void populateKvValue() throws DataException {
		Connection conn = ConnectionPool.getConnection(getDatabaseId());
		this.kvValue = WWALDApplication.get().getDataFacade().retreiveFromKvTableClob(conn, this.kvKey);		
	}

	public void setKvValue(String kvValue) {
		this.kvValue = kvValue;
	}
	
	public String getKvValue() {
		return this.kvValue;
	}

	private Component getForm() {
		Form kvForm = new Form(WicketIdConstants.KVTablePage_FORM) {
			@Override
			public void onSubmit() {
				try {
					Connection conn = ConnectionPool.getConnection(getDatabaseId());
					WWALDApplication.get().getDataFacade().upsertKvTableClob(conn, kvKey, kvValue);
					setWWALDFeedbackMessage("updated");
				} catch(DataException de) {
					String msg = "Could not set the kvtableclob value - ('" + kvKey + "','" + kvValue + "')";
					cLogger.error(msg, de);
					setResponsePage(GenericErrorPage.class);
				}
			}
		};
		TextArea textArea = new TextArea(WicketIdConstants.KVTablePage_FORM_TEXTAREA, new PropertyModel(this, "kvValue"));
		kvForm.add(textArea);
		return kvForm;
	}

	@Override
	protected Permission getRequiredPermission() {
		return Permission.SITE_ANALYTICS;
	}

}
