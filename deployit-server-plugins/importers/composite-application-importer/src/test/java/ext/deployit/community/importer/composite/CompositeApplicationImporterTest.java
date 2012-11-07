package ext.deployit.community.importer.composite;

import static com.google.common.io.Resources.getResource;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Maps;
import com.xebialabs.deployit.booter.local.LocalBooter;
import com.xebialabs.deployit.plugin.api.udm.CompositePackage;
import com.xebialabs.deployit.plugin.api.udm.Version;
import com.xebialabs.deployit.server.api.importer.ImportSource;
import com.xebialabs.deployit.server.api.importer.ImportedPackage;
import com.xebialabs.deployit.server.api.importer.Importer;
import com.xebialabs.deployit.server.api.importer.ImportingContext;
import com.xebialabs.deployit.server.api.importer.PackageInfo;

public class CompositeApplicationImporterTest {

	@BeforeClass
	public static void boot() {
		LocalBooter.bootWithoutGlobalContext();
	}

	private Importer importer = new CompositeApplicationImporter();

	@Test
	public void go() throws Exception {
		ImportSource importSource = new FileSource(new File(getResource("composite-application-1.cad").toURI()), false);
		assertTrue(importer.canHandle(importSource));

		final DefaultImportingContext context = new DefaultImportingContext();
		final PackageInfo packageInfo = importer.preparePackage(importSource, context);

		assertEquals("PetCompositeApp", packageInfo.getApplicationName());
		assertEquals("3.4", packageInfo.getApplicationVersion());


		final ImportedPackage importedPackage = importer.importEntities(packageInfo, context);

		assertThat(importedPackage.getVersion(), instanceOf(CompositePackage.class));
		CompositePackage cp = (CompositePackage) importedPackage.getVersion();
		assertThat(cp.<String>getProperty("prop1"), is("value1"));
		assertThat(cp.<String>getProperty("prop2"), is("value2"));


		assertEquals("Applications/PetCompositeApp", importedPackage.getApplication().getId());
		assertEquals("Applications/PetCompositeApp/3.4", cp.getId());
		final List<Version> versions = cp.getPackages();
		assertEquals(2, versions.size());
		assertEquals("[Applications/PetClinic-Ear/1.0, Applications/PetClinic-Ear/2.0]", versions.toString());
	}


	public class DefaultImportingContext implements ImportingContext {
		private Map<String, Object> ctx = Maps.newHashMap();

		@SuppressWarnings({"unchecked"})
		@Override
		public <T> T getAttribute(String s) {
			return (T) ctx.get(s);
		}

		@Override
		public <T> void setAttribute(String s, T t) {
			ctx.put(s, t);
		}
	}

}
