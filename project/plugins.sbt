resolvers ++= Seq(
  "netlogo-extension-plugin"        at "https://dl.cloudsmith.io/public/netlogo/netlogo-extension-plugin/maven/"
, "netlogo-extension-documentation" at "https://dl.cloudsmith.io/public/netlogo/netlogo-extension-documentation/maven/"
)

addSbtPlugin("org.nlogo" % "netlogo-extension-plugin" % "5.2.1")
addSbtPlugin("org.nlogo" % "netlogo-extension-documentation" % "0.8.3")
addSbtPlugin("org.bytedeco" % "sbt-javacpp" % "1.17")
