package application;

public enum Voices {

    Default ("(voice_kal_diphone)"),
    Kiwi_Male ("(voice_akl_nz_jdt_diphone)"),
    Kiwi_Female ("(voice_akl_nz_cw_cg_cg)")
    ;

    private final String voicesPackage;

    private Voices(String voicesPackage) {
        this.voicesPackage = voicesPackage;
    }

    public String getVoicePackage() {
        return voicesPackage;
    }
}
