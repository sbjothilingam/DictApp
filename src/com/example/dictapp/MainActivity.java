package com.example.dictapp;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	//String dictUrl="http://services.aonaware.com/DictService/DictService.asmx/Define?word=";
	String dictUrl="http://www.dictionaryapi.com/api/v1/references/thesaurus/xml/";
	String key="d7016cb1-64c0-4ecc-8838-81b0f5c67c3a";
	ProgressDialog prog=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	public InputStream openHttpConnection(String url){
		InputStream in=null;
		try {
			URL u=new URL(url);
			URLConnection conn=u.openConnection();
			if(conn instanceof HttpURLConnection){
				HttpURLConnection http=(HttpURLConnection)conn;
				http.setAllowUserInteraction(false);
				http.setInstanceFollowRedirects(true);
				http.setRequestMethod("GET");
				if(http.getResponseCode()==HttpURLConnection.HTTP_OK){
					in=http.getInputStream();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return in;
	}
	/*
	//this function is to read a text file and display it
	public String text(String url){
		String doc="";
		int index;
		InputStream i=openHttpConnection(url);
		InputStreamReader ii=new InputStreamReader(i);
		char[] buff=new char[2000];
		try{
			while((index = ii.read(buff))>0){
				String read=String.copyValueOf(buff,0,index);
				doc+=read;
				buff=new char[2000];
			}
		}catch(Exception e){
			
		}
		return doc;
	}*/
	//reading it as xml fil
	public String defXml(String url){
		String def="";
		InputStream in=openHttpConnection(url);
		/*try{
			DocumentBuilderFactory doc=DocumentBuilderFactory.newInstance();
			DocumentBuilder docbuild=doc.newDocumentBuilder();
			Document d=docbuild.parse(in);
			//puts all the text nodes in tree format
			d.getDocumentElement().normalize();
			//reads all the elements which has defeinition tag ROOT
			NodeList listdef=d.getElementsByTagName("Definition");
			for(int i=0;i<listdef.getLength();i++){
				Node definition=listdef.item(i);
				if(definition.getNodeType() == Node.ELEMENT_NODE){
					Element childdef=(Element)definition;
					//all elements which has word definition has tagname CHILD
					NodeList worddef=childdef.getElementsByTagName("WordDefinition");
					for(int j=0;j<worddef.getLength();j++){
						Element word=(Element)worddef.item(j);
						NodeList childworddef=((Node)word).getChildNodes();
						def+=((Node)childworddef.item(0)).getNodeValue()+"\n";
					}
				}
			}
		}catch(Exception e){
			
		}*/
		//acessing a dict api
		try{
			DocumentBuilderFactory fact=DocumentBuilderFactory.newInstance();
			DocumentBuilder build=fact.newDocumentBuilder();
			Document doc=build.parse(in);
			doc.getDocumentElement().normalize();
			NodeList list=doc.getElementsByTagName("sens");
			def+="<b>Meaning</b><br /><p>";
			for(int i=0;i<list.getLength();i++){
				Node ch=list.item(i);
				if(ch.getNodeType() == Node.ELEMENT_NODE){
					Element ele=(Element)ch;
					NodeList meaning=ele.getElementsByTagName("mc");
					for(int j=0;j<meaning.getLength();j++){
						Element mean=(Element)meaning.item(j);
						NodeList meaninglist=((Node)mean).getChildNodes();
						def+=((Node)meaninglist.item(0)).getNodeValue()+"<br/>";
					}
					def+="</p>";
				}
			}
		}catch(Exception e){
			
		}
		return def;
	}
	class FindDef extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... url) {
			
			//return text(url[0]);
			return defXml(url[0]);
		}
		protected void onPostExecute(String doc){
			prog.dismiss();
			TextView def=(TextView)findViewById(R.id.definition);
			def.setText(Html.fromHtml(doc));
		}
		
	}
	public void search(View v){
		TextView def=(TextView)findViewById(R.id.definition);
		def.clearComposingText();
		String ur="";
		EditText word=(EditText)findViewById(R.id.word);
		//ur+=dictUrl+word.getText().toString();
		ur+=dictUrl+word.getText().toString()+"?key="+key;
		prog= ProgressDialog.show(this, "Retrieving", "wait");
		new FindDef().execute(ur);
	}
}
