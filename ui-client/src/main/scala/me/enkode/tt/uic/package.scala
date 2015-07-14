package me.enkode.tt

import org.scalajs.dom

package object uic {

  implicit class ElementHelpers(val element: dom.Element) extends AnyVal {
    def setAttributes(attributes: Map[String, String]): dom.Element = {
      attributes foreach { case (k, v) ⇒
          element.setAttribute(k, v)
      }
      element
    }

    def appendChildren(children: Seq[dom.Node]): dom.Element = {
      children.foreach(element.appendChild)
      element
    }
  }

  implicit class DocumentHelper(val document: dom.html.Document) extends AnyVal {
    def createButton(text: String)(onClick: (dom.MouseEvent) ⇒ Unit): dom.html.Button = {
      val button = document.createElement("button").asInstanceOf[dom.html.Button]
      button.textContent = text
      button.onclick = onClick
      button
    }

    def createInput(id: String, inputType: String = "text"): dom.html.Input = {
      val input = document.createElement("input").asInstanceOf[dom.html.Input]
      input.setAttributes(Map("id" → id, "type" → inputType))
      input
    }
  }
}
