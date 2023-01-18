/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Stopwatch
 *  java.io.File
 *  java.lang.Error
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.time.Instant
 *  java.time.format.DateTimeFormatter
 *  java.time.temporal.TemporalAccessor
 *  java.util.concurrent.TimeUnit
 *  javax.xml.parsers.DocumentBuilderFactory
 *  javax.xml.parsers.ParserConfigurationException
 *  javax.xml.transform.Result
 *  javax.xml.transform.Source
 *  javax.xml.transform.Transformer
 *  javax.xml.transform.TransformerException
 *  javax.xml.transform.TransformerFactory
 *  javax.xml.transform.dom.DOMSource
 *  javax.xml.transform.stream.StreamResult
 *  org.w3c.dom.Document
 *  org.w3c.dom.Element
 *  org.w3c.dom.Node
 */
package net.minecraft.gametest.framework;

import com.google.common.base.Stopwatch;
import java.io.File;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.concurrent.TimeUnit;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.TestReporter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class JUnitLikeTestReporter
implements TestReporter {
    private final Document document;
    private final Element testSuite;
    private final Stopwatch stopwatch;
    private final File destination;

    public JUnitLikeTestReporter(File $$0) throws ParserConfigurationException {
        this.destination = $$0;
        this.document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        this.testSuite = this.document.createElement("testsuite");
        Element $$1 = this.document.createElement("testsuite");
        $$1.appendChild((Node)this.testSuite);
        this.document.appendChild((Node)$$1);
        this.testSuite.setAttribute("timestamp", DateTimeFormatter.ISO_INSTANT.format((TemporalAccessor)Instant.now()));
        this.stopwatch = Stopwatch.createStarted();
    }

    private Element createTestCase(GameTestInfo $$0, String $$1) {
        Element $$2 = this.document.createElement("testcase");
        $$2.setAttribute("name", $$1);
        $$2.setAttribute("classname", $$0.getStructureName());
        $$2.setAttribute("time", String.valueOf((double)((double)$$0.getRunTime() / 1000.0)));
        this.testSuite.appendChild((Node)$$2);
        return $$2;
    }

    @Override
    public void onTestFailed(GameTestInfo $$0) {
        Element $$4;
        String $$1 = $$0.getTestName();
        String $$2 = $$0.getError().getMessage();
        if ($$0.isRequired()) {
            Element $$3 = this.document.createElement("failure");
            $$3.setAttribute("message", $$2);
        } else {
            $$4 = this.document.createElement("skipped");
            $$4.setAttribute("message", $$2);
        }
        Element $$5 = this.createTestCase($$0, $$1);
        $$5.appendChild((Node)$$4);
    }

    @Override
    public void onTestSuccess(GameTestInfo $$0) {
        String $$1 = $$0.getTestName();
        this.createTestCase($$0, $$1);
    }

    @Override
    public void finish() {
        this.stopwatch.stop();
        this.testSuite.setAttribute("time", String.valueOf((double)((double)this.stopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000.0)));
        try {
            this.save(this.destination);
        }
        catch (TransformerException $$0) {
            throw new Error("Couldn't save test report", (Throwable)$$0);
        }
    }

    public void save(File $$0) throws TransformerException {
        TransformerFactory $$1 = TransformerFactory.newInstance();
        Transformer $$2 = $$1.newTransformer();
        DOMSource $$3 = new DOMSource((Node)this.document);
        StreamResult $$4 = new StreamResult($$0);
        $$2.transform((Source)$$3, (Result)$$4);
    }
}