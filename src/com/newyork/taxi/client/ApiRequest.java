package com.newyork.taxi.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


class ApiRequest {
    private String serverUrl;
    private Map resultMap;
    
    public ApiRequest() {
        this.serverUrl = "http://localhost:8080/TaxiTripServer/webresources/com.newyork.taxi.greentrip";
        this.resultMap= new TreeMap<String,Integer>();
    }
    
    public Map<String, String> getTaxiTripDataFromServer(String from, String to) throws MalformedURLException, IOException, SAXException, ParserConfigurationException {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet request = new HttpGet(serverUrl + "/" + from + "/" + to);
        HttpResponse response = httpClient.execute(request);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        InputSource input = new InputSource(rd);
        parseXML(input);
        return resultMap;
    }
    
    private void parseXML(InputSource input) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(input);
        doc.getDocumentElement().normalize ();
        NodeList dataByMinute = doc.getElementsByTagName("entry");
        
        for(int count=0; count<dataByMinute.getLength() ; count++){
            Node firstDataNode = dataByMinute.item(count);
            if(firstDataNode.getNodeType() == Node.ELEMENT_NODE){
                Element firstDataElement = (Element)firstDataNode;

                NodeList minuteData = firstDataElement.getElementsByTagName("key");
                Element minuteDataElement = (Element)minuteData.item(0);

                NodeList textMinuteList = minuteDataElement.getChildNodes();
                String key = ((Node)textMinuteList.item(0)).getNodeValue().trim();

                NodeList countValueData = firstDataElement.getElementsByTagName("value");
                Element countValueDataElement = (Element)countValueData.item(0);

                NodeList countValueList = countValueDataElement.getChildNodes();
                String value = ((Node)countValueList.item(0)).getNodeValue().trim();

                resultMap.put(key, value);
            }
        }
    }
}