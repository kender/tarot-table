package me.enkode.tt.uic

import me.enkode.tt.models.{Asset, AssetInstance}
import org.scalajs.dom

package object assets {
  trait Renderer[A <: Asset] {
    def render(asset: A, assetInstance: AssetInstance): dom.Element
  }
}
