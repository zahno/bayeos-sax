package de.unibayreuth.bayceer.bayeos.controller;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.junit.After;
import org.junit.Before;

public class ObjektTest {
	
	private Client client = null;
	private WebTarget target = null;
	
	@Before
	public void setup(){
		client = ClientBuilder.newClient();
		target = client.target("http://132.180.112.172/bayeos-sax/rest/objekt");		
	}
	
	@After
	public void tearDown() {
		client.close();
	}
//	
//	@Test
//	public void testFindAll() {
//	     Objekt[] obs = target.request().get(Objekt[].class);
//	     assertNotNull(obs);     
//	}
//
//	@Test
//	public void testFindById() {		
//		Objekt o = (Objekt) target.path("/id/200004").request().get(Objekt.class);		
//		assertEquals("All Folders",o.getName() );
//	}
//	
//	
	

}
