globalThis.require = require;

require.config({ paths: { vs: "vs" } });
require(["vs/editor/editor.main"], () => {
    var editor = monaco.editor.create(document.getElementById("container"), {
        language: "nbt",
        theme: "vs-dark",
        minimap: { enabled: false },
        scrollbar: { vertical: "hidden" },
    });

    monaco.languages.register({ id: "nbt", aliases: ["NBT"] });

    monaco.languages.registerColorProvider("nbt", {
        provideColorPresentations(model, colorInfo, token) {
            console.log(model, colorInfo, token);
            return [];
        },
        provideDocumentColors(model, token) {
            monaco.editor.tokenize(model.getValue(), "nbt");
            return [];
        },
    });

    monaco.languages.setLanguageConfiguration("nbt", {
        wordPattern: /(-?\d*\.\d\w*)|([^\[\{\]\}\:\"\,\s]+)/g,
        brackets: [
            ["{", "}"],
            ["[", "]"],
        ],
        autoClosingPairs: [
            { open: "{", close: "}", notIn: ["string"] },
            { open: "[", close: "]", notIn: ["string"] },
            { open: '"', close: '"', notIn: ["string"] },
        ],
    });

    monaco.languages.registerTokensProviderFactory("nbt", {
        create: () => ({
            keywords: ["byte", "short", "int", "long", "float", "double", "string", "object"],

            escapes: /\\(?:[abfnrtv\\"']|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})/,
            digits: /\d+(_+\d+)*/,
            octaldigits: /[0-7]+(_+[0-7]+)*/,
            binarydigits: /[0-1]+(_+[0-1]+)*/,
            hexdigits: /[[0-9a-fA-F]+(_+[0-9a-fA-F]+)*/,

            tokenizer: {
                root: [[/[{}]/, "delimiter.bracket"], { include: "common" }],

                common: [
                    [
                        /#?[a-z_$][\w$]*/,
                        {
                            cases: {
                                "@keywords": "keyword",
                                "@default": "identifier",
                            },
                        },
                    ],

                    { include: "@whitespace" },

                    [/[()\[\]]/, "@brackets"],
                    [/!(?=([^=]|$))/, "delimiter"],

                    [/(@digits)[eE]([\-+]?(@digits))?/, "number.float"],
                    [/(@digits)\.(@digits)([eE][\-+]?(@digits))?/, "number.float"],
                    [/0[xX](@hexdigits)n?/, "number.hex"],
                    [/0[oO]?(@octaldigits)n?/, "number.octal"],
                    [/0[bB](@binarydigits)n?/, "number.binary"],
                    [/(@digits)n?/, "number"],

                    [/[;,.]/, "delimiter"],

                    [/"([^"\\]|\\.)*$/, "string.invalid"],
                    [/'([^'\\]|\\.)*$/, "string.invalid"],
                    [/"/, "string", "@string_double"],
                ],

                whitespace: [[/[ \t\r\n]+/, ""]],

                string_double: [
                    [/[^\\"]+/, "string"],
                    [/@escapes/, "string.escape"],
                    [/\\./, "string.escape.invalid"],
                    [/"/, "string", "@pop"],
                ],
            },
        }),
    });

    editor.setValue(`{
    byte b: 1
    short s: 1623
    int i: 0x0003274
    long l: 7327483298
    float f: 0.0121789
    double d: 4378247901389481.3472747837286
    string S: "Hello, World!"
    object o: {
        string s: "example",
        int value: 42,
        object nested: {
            float f: 1.23
        }
    }

    byte[] b: [1]
    short[] s: [1623]
    int[] i: [0x0003274]
    long[] l: [7327483298]
    float[] f: [0.0121789]
    double[] d: [4378247901389481.3472747837286]
    string[] S: ["Hello, World!"]
    object[] o: [
        {
            string s: "example",
            int value: 42,
            object nested: {
                float f: 1.23
            }
        }
    ]
}`);
});
