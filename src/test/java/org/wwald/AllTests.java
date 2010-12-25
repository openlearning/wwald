package org.wwald;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.wwald.model.DataFacadeRDBMSImplTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	TestPropertiesLoading.class,
	//UsersFileParserTest.class,
	//CourseFileParserTest.class,
	DataFacadeRDBMSImplTest.class,
})
public class AllTests {

}
