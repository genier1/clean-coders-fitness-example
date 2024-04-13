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
                includeInherited("SetUp", "setup");
                if (includeSuiteSetup) {
                    includeInherited(SuiteResponder.SUITE_SETUP_NAME, "setup");
                }
            }

            buffer.append(pageData.getContent());
            if (pageData.hasAttribute("Test")) {
                includeInherited("TearDown", "teardown");
                if (includeSuiteSetup) {
                    includeInherited(SuiteResponder.SUITE_TEARDOWN_NAME, "teardown");
                }
            }

            pageData.setContent(buffer.toString());
            return pageData.getHtml();
        }

        private void includeInherited(String suiteSetupName, String mode) throws Exception {
            WikiPage suiteSetup = PageCrawlerImpl.getInheritedPage(suiteSetupName, wikiPage);
            if (suiteSetup != null) {
                includePage(suiteSetup, mode);
            }
        }

        private void includePage(WikiPage setUp, String mode) throws Exception {
            WikiPagePath pagePath = wikiPage.getPageCrawler().getFullPath(setUp);
            String pagePathName = PathParser.render(pagePath);
            buffer.append("!include -").append(mode).append(" .").append(pagePathName).append("\n");
        }
    }
}
