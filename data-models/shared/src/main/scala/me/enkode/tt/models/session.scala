package me.enkode.tt.models

case class AssetInstanceState()
case class AssetInstance(assetInstanceId: Id, assetId: Id, assetInstanceState: AssetInstanceState)
case class Session(assetInstances: Set[AssetInstance])