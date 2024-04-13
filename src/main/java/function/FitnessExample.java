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
                if (includeSuiteSetup) {
                    WikiPage suiteSetup = PageCrawlerImpl.getInheritedPage(SuiteResponder.SUITE_SETUP_NAME, wikiPage);
                    if (suiteSetup != null) {
                        includePage(suiteSetup, "setup");
                    }
                }
                WikiPage setup = PageCrawlerImpl.getInheritedPage("SetUp", wikiPage);
                if (setup != null) {
                    includePage(setup, "setup");
                }
            }

            buffer.append(pageData.getContent());
            if (pageData.hasAttribute("Test")) {
                WikiPage teardown = PageCrawlerImpl.getInheritedPage("TearDown", wikiPage);
                if (teardown != null) {
                    includePage(teardown, "teardown");
                }
                if (includeSuiteSetup) {
                    WikiPage suiteTeardown = PageCrawlerImpl.getInheritedPage(SuiteResponder.SUITE_TEARDOWN_NAME, wikiPage);
                    if (suiteTeardown != null) {
                        includePage(suiteTeardown, "teardown");
                    }
                }
            }

            pageData.setContent(buffer.toString());
            return pageData.getHtml();
        }

        private void includePage(WikiPage setUp, String mode) throws Exception {
            WikiPagePath pagePath = wikiPage.getPageCrawler().getFullPath(setUp);
            String pagePathName = PathParser.render(pagePath);
            buffer.append("!include -").append(mode).append(" .").append(pagePathName).append("\n");
        }
    }
}
