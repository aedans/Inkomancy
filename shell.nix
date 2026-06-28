{ pkgs ? import <nixpkgs> { } }:

pkgs.mkShell {
  name = "inkomancy";

  # JDK 21 is required by the build (sourceCompatibility = VERSION_21).
  # The project uses the Gradle wrapper (./gradlew), so Gradle itself is
  # provided too for convenience but the wrapper will be used by default.
  packages = with pkgs; [
    jdk21
    gradle
  ];

  # Native libraries needed to launch the dev client (LWJGL/GLFW) via
  # `./gradlew :fabric:runClient` or `:neoforge:runClient`.
  buildInputs = with pkgs; [
    glfw
    libGL
    openal
    xorg.libX11
    xorg.libXcursor
    xorg.libXext
    xorg.libXrandr
    xorg.libXxf86vm
    xorg.libXi
  ];

  shellHook = ''
    export JAVA_HOME="${pkgs.jdk21}"
    # Let LWJGL find the system OpenGL/GLFW/OpenAL at runtime.
    export LD_LIBRARY_PATH="${pkgs.lib.makeLibraryPath [
      pkgs.glfw
      pkgs.libGL
      pkgs.openal
      pkgs.xorg.libX11
      pkgs.xorg.libXcursor
      pkgs.xorg.libXext
      pkgs.xorg.libXrandr
      pkgs.xorg.libXxf86vm
      pkgs.xorg.libXi
    ]}:$LD_LIBRARY_PATH"
  '';
}
