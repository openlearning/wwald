package org.wwald;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.wwald.model.DataFacadeRDBMSImplTest;
import org.wwald.service.ApplicationFacadeTest;
import org.wwald.util.TestCourseWikiParser;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	TestPropertiesLoading.class,
	//UsersFileParserTest.class,
	//CourseFileParserTest.class,
	DataFacadeRDBMSImplTest.class,
	ApplicationFacadeTest.class,
	TestCourseWikiParser.class,
})
public class AllTests {

}
