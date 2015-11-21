package pl.edu.agh.ki.bd.htmlIndexer.persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.stereotype.Component;
import pl.edu.agh.ki.bd.htmlIndexer.model.ProcessedUrl;
import pl.edu.agh.ki.bd.htmlIndexer.model.Sentence;
import pl.edu.agh.ki.bd.htmlIndexer.model.Word;

@Component
public class SessionProvider {
	
	private final SessionFactory sessionFactory;
	
	public SessionProvider() {
        StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();
        registryBuilder.configure("hibernate.cfg.xml");
        StandardServiceRegistry registry = registryBuilder.build();

        MetadataSources metadataSources = new MetadataSources(registry);
        metadataSources.addAnnotatedClass(Sentence.class)
                .addAnnotatedClass(ProcessedUrl.class)
                .addAnnotatedClass(Word.class);

        SessionFactoryBuilder sfBuilder = metadataSources.getMetadataBuilder().build().getSessionFactoryBuilder();
        sessionFactory = sfBuilder.build();

        sessionFactory.openSession().close();
	}
	

	public Session getSession() {
		return this.sessionFactory.openSession();
	}

	public void shutdown() {
		this.sessionFactory.close();
	}
}
