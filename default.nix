{
  pkgs ? import <nixpkgs> { },
}:

pkgs.mkShell {
  buildInputs = with pkgs; [
    maven
    jdk17
    openjfx17
    glib
  ];

  shellHook = ''
    export PATH="$PATH:${pkgs.maven}/bin"
    export JAVA_HOME="${pkgs.jdk17}/lib/openjdk"
    export PATH="$PATH:$JAVA_HOME/bin"
    export LD_LIBRARY_PATH="${pkgs.libGL}/lib:${pkgs.gtk3}/lib:${pkgs.glib.out}/lib:${pkgs.xorg.libXtst}/lib:$LD_LIBRARY_PATH"  # Add libXtst to LD_LIBRARY_PATH
    export JAVAFX_PATH="${pkgs.openjfx17}/lib"
  '';
}
