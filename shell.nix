# Pinned to nixos-26.05 @ 4062d36 so jdk21 always resolves to the same
# store path (keeps .vscode/settings.json's java.jdt.ls.java.home stable).
{ pkgs ? import (fetchTarball {
    url = "https://github.com/NixOS/nixpkgs/archive/4062d36ebeae843c750011eef6b61ec9a9dbc9a9.tar.gz";
    sha256 = "0hha7lam2c2655f7m0w9jkn8pacmprzgcg3fg7jrnv479fcdh8y2";
  }) { } }:

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
