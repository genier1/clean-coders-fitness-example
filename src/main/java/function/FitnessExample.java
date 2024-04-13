package function;

import fitnesse.responders.run.SuiteResponder;
import fitnesse.wiki.*;

public class FitnessExample {
    public String testableHtml(PageData pageData, boolean includeSuiteSetup) throws Exception {
        return new TestableHtmlBuilder(pageData, includeSuiteSetup).createPage();
    }

    private class TestableHtmlBuilder {
        private PageData pageData;
        private boolean includeSuiteSetup;
        private WikiPage wikiPage;
        private StringBuffer buffer;

        public TestableHtmlBuilder(PageData pageData, boolean includeSuiteSetup) {
            this.wikiPage = pageData.getWikiPage();
            this.pageData = pageData;
            this.includeSuiteSetup = includeSuiteSetup;
            this.buffer = new StringBuffer();
        }

        public String createPage() throws Exception {
            if (pageData.hasAttribute("Test")) {
                String setUp = "SetUp";
                String mode = "setup";
                if (includeSuiteSetup) {
                    String suiteSetupName = SuiteResponder.SUITE_SETUP_NAME;
                    WikiPage suiteSetup = PageCrawlerImpl.getInheritedPage(suiteSetupName, wikiPage);
                    includeInherited(suiteSetup, mode);
                }
                WikiPage setup = PageCrawlerImpl.getInheritedPage(setUp, wikiPage);
                includeInherited(setup, mode);
            }

            buffer.append(pageData.getContent());
            if (pageData.hasAttribute("Test")) {
                String tearDown = "TearDown";
                String mode = "teardown";
                WikiPage teardown = PageCrawlerImpl.getInheritedPage(tearDown, wikiPage);
                includeInherited(teardown, mode);
                if (includeSuiteSetup) {
                    String mode1 = "teardown";
                    String suiteTeardownName = SuiteResponder.SUITE_TEARDOWN_NAME;
                    WikiPage suiteTeardown = PageCrawlerImpl.getInheritedPage(suiteTeardownName, wikiPage);
                    includeInherited(suiteTeardown, mode1);
                }
            }

            pageData.setContent(buffer.toString());
            return pageData.getHtml();
        }

        private void includeInherited(WikiPage teardown, String mode) throws Exception {
            if (teardown != null) {
                includePage(teardown, mode);
            }
        }

        private void includePage(WikiPage setUp, String mode) throws Exception {
            WikiPagePath pagePath = wikiPage.getPageCrawler().getFullPath(setUp);
            String pagePathName = PathParser.render(pagePath);
            buffer.append("!include -").append(mode).append(" .").append(pagePathName).append("\n");
        }
    }
}
