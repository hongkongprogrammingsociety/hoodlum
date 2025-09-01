package com.hoodlum;

import com.hoodlum.antlr.HoodlumBaseVisitor;
import com.hoodlum.antlr.HoodlumParser;
import org.objectweb.asm.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.objectweb.asm.Opcodes.*;

/**
 * Compiles Hoodlum AST to JVM bytecode using ASM.
 * Currently supports: PRINT statements with string literals and numbers.
 */
public class BytecodeCompiler extends HoodlumBaseVisitor<Void> {
    private final ClassWriter classWriter;
    private final MethodVisitor mainMethod;
    private final String className;

    public BytecodeCompiler(String className) {
        this.className = className;
        this.classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        
        // Create class header
        classWriter.visit(V17, ACC_PUBLIC | ACC_SUPER, className, null, "java/lang/Object", null);
        
        // Create default constructor
        MethodVisitor constructor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        constructor.visitCode();
        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        constructor.visitInsn(RETURN);
        constructor.visitMaxs(0, 0);
        constructor.visitEnd();
        
        // Create main method
        this.mainMethod = classWriter.visitMethod(ACC_PUBLIC | ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        mainMethod.visitCode();
    }

    public byte[] compile() {
        // Finish main method
        mainMethod.visitInsn(RETURN);
        mainMethod.visitMaxs(0, 0);
        mainMethod.visitEnd();
        
        classWriter.visitEnd();
        return classWriter.toByteArray();
    }

    public void writeToFile(Path path) throws IOException {
        Files.write(path, compile());
    }

    @Override
    public Void visitProgram(HoodlumParser.ProgramContext ctx) {
        // Visit all statements
        for (int i = 0; i < ctx.getChildCount(); i++) {
            visit(ctx.getChild(i));
        }
        return null;
    }

    @Override
    public Void visitPrintStmtStmt(HoodlumParser.PrintStmtStmtContext ctx) {
        var printStmt = ctx.printStmt();
        
        // Get System.out
        mainMethod.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        
        if (printStmt.portfolioKw != null) {
            // PRINT portfolio
            mainMethod.visitLdcInsn("Portfolio: (not implemented in bytecode mode)");
        } else if (printStmt.expr() != null) {
            // PRINT expr - for now just handle literals
            visit(printStmt.expr());
        } else {
            mainMethod.visitLdcInsn("(empty print)");
        }
        
        // Call println
        mainMethod.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        
        return null;
    }

    @Override
    public Void visitForStmtStmt(HoodlumParser.ForStmtStmtContext ctx) {
        // FOR x = start TO end ... NEXT
        String varName = ctx.forStmt().ID().getText();
        HoodlumParser.ExprContext startExpr = ctx.forStmt().expr(0);
        HoodlumParser.ExprContext endExpr = ctx.forStmt().expr(1);

        // Use local variable slot 1 for loop variable (slot 0 is 'args')
        int varSlot = 1;

        // Initialize loop variable
        mainMethod.visitLdcInsn(Integer.parseInt(startExpr.getText()));
        mainMethod.visitVarInsn(ISTORE, varSlot);

        Label loopStart = new Label();
        Label loopEnd = new Label();
        mainMethod.visitLabel(loopStart);

        // Check loop condition: x <= end
        mainMethod.visitVarInsn(ILOAD, varSlot);
        mainMethod.visitLdcInsn(Integer.parseInt(endExpr.getText()));
        mainMethod.visitJumpInsn(IF_ICMPGT, loopEnd);

        // Loop body: visit all statements inside the loop
        if (ctx.forStmt().forBody() != null) {
            for (HoodlumParser.StatementContext stmtCtx : ctx.forStmt().forBody().statement()) {
                visit(stmtCtx);
            }
        }

        // Increment loop variable
        mainMethod.visitIincInsn(varSlot, 1);
        mainMethod.visitJumpInsn(GOTO, loopStart);

        mainMethod.visitLabel(loopEnd);
        return null;
    }

    @Override
    public Void visitExpr(HoodlumParser.ExprContext ctx) {
        if (ctx.NUMBER() != null) {
            // Convert number to string for printing
            String number = ctx.NUMBER().getText();
            mainMethod.visitLdcInsn(number);
        } else if (ctx.INT() != null) {
            // Convert int to string for printing
            String intValue = ctx.INT().getText();
			System.out.println("s2="+intValue);
            mainMethod.visitLdcInsn(intValue);
        } else if (ctx.ID() != null) {
            // Variable reference - not implemented yet
            String varName = ctx.ID().getText();
            mainMethod.visitLdcInsn("Variable(" + varName + ")");
        } else if (ctx.CASH() != null) {
            // Cash variable
            mainMethod.visitLdcInsn("10000.0");
        } else {
            // Other expressions not implemented yet
            mainMethod.visitLdcInsn("(expression not implemented)");
        }
        return null;
    }
}
