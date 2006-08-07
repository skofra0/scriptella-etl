/*
 * Copyright 2006 The Scriptella Project Team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package scriptella.configuration;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Represents XML element
 *
 * @author Fyodor Kupolov
 * @version 1.0
 */
public class XMLElement {
    private Element element;
    private URL documentURL;

    public XMLElement(Element element, URL documentURI) {
        this.element = element;
        this.documentURL = documentURI;
    }

    public XMLElement(Element element, XMLElement parent) {
        this.element = element;
        documentURL = parent.documentURL;
    }

    public String getTagName() {
        return element.getTagName();
    }

    public Element getElement() {
        return element;
    }

    public URL getDocumentURL() {
        return documentURL;
    }

    protected List<XMLElement> getChildren() {
        return asList(element.getChildNodes());
    }

    public String getXPath() {
        List<String> l = new ArrayList<String>();
        Node cur = element;
        StringBuilder tmp = new StringBuilder();

        while (!(cur instanceof Document)) {
            int pos = 1;
            Node sib = element;
            final String curTagName = ((Element) cur).getTagName();

            while (sib != null) {
                sib = sib.getPreviousSibling();

                if ((sib != null) && sib instanceof Element) {
                    Element ee = (Element) sib;

                    if (curTagName.equals(ee.getTagName())) {
                        pos++;
                    }
                }
            }

            tmp.setLength(0);
            tmp.append(curTagName);
            tmp.append('[');
            tmp.append(pos);
            tmp.append(']');
            l.add(tmp.toString());
            cur = cur.getParentNode();
        }

        StringBuilder res = new StringBuilder(100);

        for (int i = l.size() - 1; i >= 0; i--) {
            res.append('/');
            res.append(l.get(i));
        }

        return res.toString();
    }

    public List<XMLElement> getChildren(final String name) {
        List<XMLElement> res = new ArrayList<XMLElement>();
        Node node = element.getFirstChild();

        while (node != null) {
            if (node instanceof Element) {
                if (name.equals(((Element) node).getTagName())) {
                    res.add(new XMLElement((Element) node, documentURL));
                }
            }

            node = node.getNextSibling();
        }

        return res;
    }

    public List<XMLElement> getChildren(final Set<String> names) {
        List<XMLElement> res = new ArrayList<XMLElement>();
        Node node = element.getFirstChild();

        while (node != null) {
            if (node instanceof Element) {
                if (names.contains(((Element) node).getTagName())) {
                    res.add(new XMLElement((Element) node, documentURL));
                }
            }

            node = node.getNextSibling();
        }

        return res;
    }

    public XMLElement getChild(final String name) {
        Node node = element.getFirstChild();

        while (node != null) {
            if (node instanceof Element) {
                if (name.equals(((Element) node).getTagName())) {
                    return new XMLElement((Element) node, documentURL);
                }
            }

            node = node.getNextSibling();
        }

        return null;
    }

    protected List<XMLElement> asList(final NodeList list) {
        List<XMLElement> result = new ArrayList<XMLElement>();
        Node node = element.getFirstChild();

        while (node != null) {
            if (node instanceof Element) {
                result.add(new XMLElement((Element) node, documentURL));
            }

            node = node.getNextSibling();
        }

        return result;
    }

    public String getAttribute(final String attribute) {
        final String a = element.getAttribute(attribute);

        return ((a != null) && (a.length() == 0)) ? null : a;
    }

    protected boolean getBooleanProperty(final String attribute,
                                         final boolean defaultValue) {
        final String a = getAttribute(attribute);

        if (a == null) {
            return defaultValue;
        }

        if ("true".equalsIgnoreCase(a) || "1".equalsIgnoreCase(a) ||
                "on".equalsIgnoreCase(a) || "yes".equalsIgnoreCase(a)) {
            return true;
        }

        if ("false".equalsIgnoreCase(a) || "0".equalsIgnoreCase(a) ||
                "off".equalsIgnoreCase(a) || "no".equalsIgnoreCase(a)) {
            return false;
        }

        throw new ConfigurationException("Unrecognized value '" + a +
                "' of boolean attribute " + attribute +
                ". Valid values: yes/no, true/false, 1/0, on/off", this);
    }
}
