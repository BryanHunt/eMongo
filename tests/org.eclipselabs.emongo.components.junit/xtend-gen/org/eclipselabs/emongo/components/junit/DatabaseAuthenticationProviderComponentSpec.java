package org.eclipselabs.emongo.components.junit;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.xtext.xbase.lib.Functions.Function0;
import org.eclipselabs.emongo.components.DatabaseAuthenticationProvider;
import org.eclipselabs.emongo.components.DatabaseAuthenticationProviderComponent;
import org.hamcrest.StringDescription;
import org.jnario.lib.Should;
import org.jnario.runner.Named;
import org.jnario.runner.Order;
import org.jnario.runner.Subject;
import org.junit.Assert;

/**
 * The **DatabaseAuthenticationProvider** provides MongoDB credentials
 * as a configured OSGi service.  There are three service properties
 * that must be read by the service: uri, user, and password.
 */
@SuppressWarnings("all")
@Named("DatabaseAuthenticationProviderComponent")
public class DatabaseAuthenticationProviderComponentSpec {
  @Subject
  public DatabaseAuthenticationProviderComponent subject;
  
  Map<String,Object> properties = new Function0<Map<String,Object>>() {
    public Map<String,Object> apply() {
      HashMap<String,Object> _hashMap = new HashMap<String,Object>();
      return _hashMap;
    }
  }.apply();
  
  public void before() throws Exception {
    this.properties.put(DatabaseAuthenticationProvider.PROP_URI, "mongodb://localhost/db");
    this.properties.put(DatabaseAuthenticationProvider.PROP_USER, "user");
    this.properties.put(DatabaseAuthenticationProvider.PROP_PASSWORD, "password");
  }
  
  @Named("configuration parameters are available through the API")
  @Order(1)
  public void _configurationParametersAreAvailableThroughTheAPI() throws Exception {
    this.subject.configure(this.properties);
    String _uRI = this.subject.getURI();
    boolean _should_be = Should.should_be(_uRI, "mongodb://localhost/db");
    Assert.assertTrue("\nExpected subject.URI should be \"mongodb://localhost/db\" but"
     + "\n     subject.URI is " + new StringDescription().appendValue(_uRI).toString()
     + "\n     subject is " + new StringDescription().appendValue(this.subject).toString() + "\n", _should_be);
    
    String _user = this.subject.getUser();
    boolean _should_be_1 = Should.should_be(_user, "user");
    Assert.assertTrue("\nExpected subject.user should be \"user\" but"
     + "\n     subject.user is " + new StringDescription().appendValue(_user).toString()
     + "\n     subject is " + new StringDescription().appendValue(this.subject).toString() + "\n", _should_be_1);
    
    String _password = this.subject.getPassword();
    boolean _should_be_2 = Should.should_be(_password, "password");
    Assert.assertTrue("\nExpected subject.password should be \"password\" but"
     + "\n     subject.password is " + new StringDescription().appendValue(_password).toString()
     + "\n     subject is " + new StringDescription().appendValue(this.subject).toString() + "\n", _should_be_2);
    
  }
  
  @Named("configure throws exception when URI is missing the database name")
  @Order(2)
  public void _configureThrowsExceptionWhenURIIsMissingTheDatabaseName() throws Exception {
    this.properties.put(DatabaseAuthenticationProvider.PROP_URI, "mongodb://localhost/");
    try{
      this.subject.configure(this.properties);
      Assert.fail("Expected " + IllegalStateException.class.getName() + " in \n     subject.configure(properties)\n with:"
       + "\n     subject is " + new StringDescription().appendValue(this.subject).toString()
       + "\n     properties is " + new StringDescription().appendValue(this.properties).toString());
    }catch(IllegalStateException e){
    }
  }
  
  @Named("configure throws exception when URI is missing the database segment")
  @Order(3)
  public void _configureThrowsExceptionWhenURIIsMissingTheDatabaseSegment() throws Exception {
    this.properties.put(DatabaseAuthenticationProvider.PROP_URI, "mongodb://localhost");
    try{
      this.subject.configure(this.properties);
      Assert.fail("Expected " + IllegalStateException.class.getName() + " in \n     subject.configure(properties)\n with:"
       + "\n     subject is " + new StringDescription().appendValue(this.subject).toString()
       + "\n     properties is " + new StringDescription().appendValue(this.properties).toString());
    }catch(IllegalStateException e){
    }
  }
  
  @Named("configure throws exception when URI is missing")
  @Order(4)
  public void _configureThrowsExceptionWhenURIIsMissing() throws Exception {
    this.properties.put(DatabaseAuthenticationProvider.PROP_URI, null);
    try{
      this.subject.configure(this.properties);
      Assert.fail("Expected " + IllegalStateException.class.getName() + " in \n     subject.configure(properties)\n with:"
       + "\n     subject is " + new StringDescription().appendValue(this.subject).toString()
       + "\n     properties is " + new StringDescription().appendValue(this.properties).toString());
    }catch(IllegalStateException e){
    }
  }
  
  @Named("configure throws exception when user is missing")
  @Order(5)
  public void _configureThrowsExceptionWhenUserIsMissing() throws Exception {
    this.properties.put(DatabaseAuthenticationProvider.PROP_USER, null);
    try{
      this.subject.configure(this.properties);
      Assert.fail("Expected " + IllegalStateException.class.getName() + " in \n     subject.configure(properties)\n with:"
       + "\n     subject is " + new StringDescription().appendValue(this.subject).toString()
       + "\n     properties is " + new StringDescription().appendValue(this.properties).toString());
    }catch(IllegalStateException e){
    }
  }
  
  @Named("configure throws exception when password is missing")
  @Order(6)
  public void _configureThrowsExceptionWhenPasswordIsMissing() throws Exception {
    this.properties.put(DatabaseAuthenticationProvider.PROP_PASSWORD, null);
    try{
      this.subject.configure(this.properties);
      Assert.fail("Expected " + IllegalStateException.class.getName() + " in \n     subject.configure(properties)\n with:"
       + "\n     subject is " + new StringDescription().appendValue(this.subject).toString()
       + "\n     properties is " + new StringDescription().appendValue(this.properties).toString());
    }catch(IllegalStateException e){
    }
  }
}
