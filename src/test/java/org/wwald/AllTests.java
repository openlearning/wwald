package org.wwald;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.wwald.model.DataFacadeRDBMSImplTest;
import org.wwald.service.ApplicationFacadeTest;
import org.wwald.util.TestCourseWikiParser;

import util.TestObjectsRepository;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	TestHomePage.class,
	TestPropertiesLoading.class,
	//UsersFileParserTest.class,
	//CourseFileParserTest.class,
	DataFacadeRDBMSImplTest.class,
	ApplicationFacadeTest.class,
	TestCourseWikiParser.class,
	TestObjectsRepository.class,
})
public class AllTests {

}
