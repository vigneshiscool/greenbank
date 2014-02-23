
package utility;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import exception.ApplicationException;

/**
 * @author Vignesh
 * 
 * 
 */
public class Hibernate_Utility {
	
	 private static SessionFactory sessionFactory = null;
	    private static ServiceRegistry serviceRegistry;

	    /**
	     * @return
	     * @throws ApplicationException
	     */
	    private static SessionFactory buildSessionFactory() throws ApplicationException{
	        try {
	            // Create the SessionFactory from hibernate.cfg.xml
	        	 Configuration configuration = new Configuration();
	        	    configuration.configure();
	        	    serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();        
	        	    sessionFactory = configuration.buildSessionFactory(serviceRegistry);
	        	    return sessionFactory;
	        }
	        catch (Throwable ex) {
 	            throw new ApplicationException("Initial SessionFactory creation failed.",ex);
	        }
	    }
	    
	    /**
	     * @return
	     * @throws ApplicationException
	     */
	    public static SessionFactory getSessionFactory() throws ApplicationException{
	        if( sessionFactory == null) {
	        	sessionFactory = buildSessionFactory();
	        }
	    	return sessionFactory;
	    }
}
