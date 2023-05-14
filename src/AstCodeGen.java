import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.InvocationTargetException;

public class AstCodeGen extends ClassLoader implements Opcodes, Exp.Visitor<Object> {

    private final Environment environment;
    public ClassWriter cw;
    public MethodVisitor mv;
    private Type returnResult;

    public AstCodeGen(Environment environment){
        this.environment = environment;
    }
    public void interpret(Exp exp){
        cw = new ClassWriter(ClassWriter.COMPUTE_MAXS |
                                  ClassWriter.COMPUTE_FRAMES);
        cw.visit(
                Opcodes.V17,
                Opcodes.ACC_PUBLIC,
                "k_prog",
                null,
                "java/lang/Object",
                null);
        MethodVisitor constructor =
                cw.visitMethod(
                        Opcodes.ACC_PUBLIC,
                        "<init>",
                        "()V",
                        null,
                        null);

        constructor.visitCode();

        //super()
        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitMethodInsn(INVOKESPECIAL,
                "java/lang/Object", "<init>", "()V", false);
        constructor.visitInsn(RETURN);

        constructor.visitMaxs(0, 0);
        constructor.visitEnd();

        mv = cw.visitMethod(
                ACC_PUBLIC + ACC_STATIC,
                "func", "()LA;", null, null);
        mv.visitCode();

        exp.accept(this);
    }

    public void finishAndRun() {
        mv.visitTypeInsn(NEW,"A");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ASTORE,0);
        mv.visitInsn(SWAP);
        switch (returnResult){
            case INT   -> mv.visitMethodInsn(INVOKESPECIAL,"A", "<init>","(I)V", false);
            case ILIST -> mv.visitMethodInsn(INVOKESPECIAL,"A", "<init>","([I)V", false);
            default    -> mv.visitMethodInsn(INVOKESPECIAL,"A", "<init>","()V", false);
        }
        mv.visitVarInsn(ALOAD,0);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        cw.visitEnd();
        byte[] bytecode = cw.toByteArray();
        Class<?> code = defineClass("k_prog",
                bytecode, 0, bytecode.length);
        //File outputFile = new File("./k_prog.class");
        //try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
        //    outputStream.write(bytecode);
        //} catch (IOException e) {
        //    throw new RuntimeException(e);
        //}
        try {
            Object[] no = {};
            A a = (A)code.getMethod("func",  null).invoke(code);
            if (a.type == Type.ILIST){
                for (int i : a.ilist) {
                    System.out.print(i);
                    System.out.print(" ");
                }
                System.out.println();
            } else {
                System.out.println(a.isingle);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object visitDyadExp(DyadExp expr) {
        switch (expr.op){
            case SymOpExp s -> {

            }
            case OpExp s -> {
                if (expr.left.type==Type.ILIST && expr.right.type == Type.ILIST){
                    returnResult = Type.ILIST;
                    expr.left.accept(this);
                    mv.visitVarInsn(ASTORE,0);
                    expr.right.accept(this);
                    mv.visitInsn(DUP);
                    mv.visitVarInsn(ASTORE,1);
                    //4: aload_0
                    //5: arraylength
                    //6: istore_3
                    mv.visitInsn(ARRAYLENGTH);
                    mv.visitInsn(DUP);
                    mv.visitVarInsn(ISTORE,3);
                    //0: iconst_3
                    //1: newarray       int
                    //3: astore_2
                    mv.visitIntInsn(NEWARRAY, T_INT);
                    mv.visitVarInsn(ASTORE,2);
                    //7: iconst_0
                    //8: istore        4
                    //10: iload         4
                    mv.visitInsn(ICONST_0);
                    mv.visitVarInsn(ISTORE,4);
                    Label begin = new Label();
                    Label end = new Label();
                    mv.visitLabel(begin);
                    mv.visitVarInsn(ILOAD,4);
                    //12: iload_3
                    //13: if_icmpge     35
                    mv.visitVarInsn(ILOAD,3);
                    mv.visitJumpInsn(IF_ICMPGE,end);
                    //16: aload_2
                    //17: iload         4
                    mv.visitIntInsn(ALOAD,2);
                    mv.visitVarInsn(ILOAD,4);
                    //19: aload_0
                    //20: iload         4
                    //22: iaload
                    mv.visitIntInsn(ALOAD,0);
                    mv.visitVarInsn(ILOAD,4);
                    mv.visitInsn(IALOAD);
                    //23: aload_1
                    //24: iload         4
                    //26: iaload
                    mv.visitIntInsn(ALOAD,1);
                    mv.visitVarInsn(ILOAD,4);
                    mv.visitInsn(IALOAD);
                    //27: iadd
                    mv.visitInsn(IADD);
                    //28: iastore
                    mv.visitInsn(IASTORE);
                    //29: iinc          4, 1
                    mv.visitIincInsn(4,1);
                    //32: goto          10
                    mv.visitJumpInsn(GOTO,begin);
                    mv.visitLabel(end);
                    //35: aload_2
                    mv.visitVarInsn(ALOAD,2);
                    //36: areturn
                } else if (expr.left.type == Type.INT && expr.right.type == Type.ILIST) {
                    returnResult = Type.ILIST;
                    expr.left.accept(this);
                    mv.visitVarInsn(ISTORE,5);
                    expr.right.accept(this);
                    mv.visitInsn(DUP);
                    mv.visitVarInsn(ASTORE,1);
                    //4: aload_0
                    //5: arraylength
                    //6: istore_3
                    mv.visitInsn(ARRAYLENGTH);
                    mv.visitInsn(DUP);
                    mv.visitVarInsn(ISTORE,3);
                    //0: iconst_3
                    //1: newarray       int
                    //3: astore_2
                    mv.visitIntInsn(NEWARRAY, T_INT);
                    mv.visitVarInsn(ASTORE,2);
                    //7: iconst_0
                    //8: istore        4
                    //10: iload         4
                    mv.visitInsn(ICONST_0);
                    mv.visitVarInsn(ISTORE,4);
                    Label begin = new Label();
                    Label end = new Label();
                    mv.visitLabel(begin);
                    mv.visitVarInsn(ILOAD,4);
                    //12: iload_3
                    //13: if_icmpge     35
                    mv.visitVarInsn(ILOAD,3);
                    mv.visitJumpInsn(IF_ICMPGE,end);
                    //16: aload_2
                    //17: iload         4
                    mv.visitIntInsn(ALOAD,2);
                    mv.visitVarInsn(ILOAD,4);
                    //19: aload_0
                    //20: iload         4
                    //22: iaload
                    mv.visitIntInsn(ILOAD,5);
                    //23: aload_1
                    //24: iload         4
                    //26: iaload
                    mv.visitIntInsn(ALOAD,1);
                    mv.visitVarInsn(ILOAD,4);
                    mv.visitInsn(IALOAD);
                    //27: iadd
                    mv.visitInsn(IADD);
                    //28: iastore
                    mv.visitInsn(IASTORE);
                    //29: iinc          4, 1
                    mv.visitIincInsn(4,1);
                    //32: goto          10
                    mv.visitJumpInsn(GOTO,begin);
                    mv.visitLabel(end);
                    //35: aload_2
                    mv.visitVarInsn(ALOAD,2);
                    //36: areturn
                } else if (expr.left.type == Type.ILIST && expr.right.type == Type.INT) {
                    returnResult = Type.ILIST;
                    expr.right.accept(this);
                    mv.visitVarInsn(ISTORE,5);
                    expr.left.accept(this);
                    mv.visitInsn(DUP);
                    mv.visitVarInsn(ASTORE,1);
                    //4: aload_0
                    //5: arraylength
                    //6: istore_3
                    mv.visitInsn(ARRAYLENGTH);
                    mv.visitInsn(DUP);
                    mv.visitVarInsn(ISTORE,3);
                    //0: iconst_3
                    //1: newarray       int
                    //3: astore_2
                    mv.visitIntInsn(NEWARRAY, T_INT);
                    mv.visitVarInsn(ASTORE,2);
                    //7: iconst_0
                    //8: istore        4
                    //10: iload         4
                    mv.visitInsn(ICONST_0);
                    mv.visitVarInsn(ISTORE,4);
                    Label begin = new Label();
                    Label end = new Label();
                    mv.visitLabel(begin);
                    mv.visitVarInsn(ILOAD,4);
                    //12: iload_3
                    //13: if_icmpge     35
                    mv.visitVarInsn(ILOAD,3);
                    mv.visitJumpInsn(IF_ICMPGE,end);
                    //16: aload_2
                    //17: iload         4
                    mv.visitIntInsn(ALOAD,2);
                    mv.visitVarInsn(ILOAD,4);
                    //19: aload_0
                    //20: iload         4
                    //22: iaload
                    mv.visitIntInsn(ILOAD,5);
                    //23: aload_1
                    //24: iload         4
                    //26: iaload
                    mv.visitIntInsn(ALOAD,1);
                    mv.visitVarInsn(ILOAD,4);
                    mv.visitInsn(IALOAD);
                    //27: iadd
                    mv.visitInsn(IADD);
                    //28: iastore
                    mv.visitInsn(IASTORE);
                    //29: iinc          4, 1
                    mv.visitIincInsn(4,1);
                    //32: goto          10
                    mv.visitJumpInsn(GOTO,begin);
                    mv.visitLabel(end);
                    //35: aload_2
                    mv.visitVarInsn(ALOAD,2);
                    //36: areturn
                } else if (expr.left.type == Type.INT && expr.right.type == Type.INT) {
                    returnResult = Type.INT;
                    expr.left.accept(this);
                    expr.right.accept(this);
                    switch (s.name){
                        case "+" -> {
                            mv.visitInsn(IADD);
                        }
                        default ->{ throw new ArithmeticException("unexpected operator "+s);}
                    }
                } else {
                    throw new IllegalStateException("Unexpected types: " + expr.left.type + " " + expr.right.type);
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + expr.op);
        }
        return null;
    }

    @Override
    public Object visitMonadExp(MonadExp expr) {
        //double res = 0;
        switch (expr.op){
        //    case SymOpExp s -> {
        //        if (expr.exp == null) {
        //            res = (double)expr.op.accept(this);
        //        }
        //        else res = 2*(double)expr.exp.accept(this);
        //    }
            case OpExp s -> {
                switch(s.name) {
                    case "!" -> {
                        expr.exp.accept(this);
                        returnResult = Type.ILIST;
                        mv.visitInsn(DUP);
                        mv.visitVarInsn(ISTORE, 3); // len
                        mv.visitIntInsn(NEWARRAY, T_INT);
                        mv.visitVarInsn(ASTORE, 2); // res
                        mv.visitInsn(ICONST_0);
                        mv.visitVarInsn(ISTORE, 4); // cnt
                        Label begin = new Label();
                        Label end = new Label();
                        mv.visitLabel(begin);
                        mv.visitVarInsn(ILOAD, 4);
                        mv.visitVarInsn(ILOAD, 3);
                        mv.visitJumpInsn(IF_ICMPGE, end);
                        mv.visitIntInsn(ALOAD, 2);
                        mv.visitVarInsn(ILOAD, 4);
                        mv.visitVarInsn(ILOAD, 4);
                        mv.visitInsn(IASTORE);
                        mv.visitIincInsn(4, 1);
                        mv.visitJumpInsn(GOTO, begin);
                        mv.visitLabel(end);
                        mv.visitVarInsn(ALOAD, 2);
                    }
                    case "*" -> {
                       if (expr.type == Type.ILIST){
                           expr.exp.accept(this);
                           returnResult = Type.INT;
                           mv.visitInsn(ICONST_0);
                           mv.visitInsn(IALOAD);
                       } else {
                           expr.exp.accept(this);
                           returnResult = Type.INT;
                       }
                    }
                    default -> {}
                }
            }
            default -> {}//res = (double) expr.exp.accept(this);
        }
        //return res;
        return null;
    }

    @Override
    public Object visitNounExp(NounExp expr) {
        A v = expr.getValue();
        returnResult = expr.type;
        if (v.scalar){
            mv.visitLdcInsn(v.isingle);
        } else {
            mv.visitLdcInsn(v.ilist.length);
            mv.visitIntInsn(NEWARRAY,T_INT);
            for (int i=0; i<v.ilist.length; ++i){
                mv.visitInsn(DUP);
                mv.visitLdcInsn(i);
                mv.visitLdcInsn(v.ilist[i]);
                mv.visitInsn(IASTORE);
            }
            //mv.visitInsn(ICONST_0);
            //mv.visitInsn(IALOAD);
        }
        return null;
    }

    @Override
    public Object visitAdverb(Adverb expr) {
        return null;
    }

    @Override
    public Object visitAssignExp(AssignExp expr) {
        //A result = (A)expr.exp.accept(this);
        //environment.assign(expr.name,result);
        //return result;
        return null;
    }

    @Override
    public Object visitEachExp(EachExp expr) {
        return null;
    }

    @Override
    public Object visitEachLeftExp(EachLeftExp expr) {
        return null;
    }

    @Override
    public Object visitEachPairExp(EachPairExp expr) {
        return null;
    }

    @Override
    public Object visitEachRightExp(EachRightExp expr) {
        return null;
    }

    @Override
    public Object visitFuncCallExp(FuncCallExp expr) {
        return null;
    }

    @Override
    public Object visitFuncExp(FuncExp expr) {
        return null;
    }

    @Override
    public Object visitListExp(ListExp expr) {
        return null;
    }

    @Override
    public Object visitOverExp(OverExp expr) {
        return null;
    }

    @Override
    public Object visitScanExp(ScanExp expr) {
        return null;
    }

    @Override
    public Object visitSymExp(SymExp expr) {
        return null;
    }

    @Override
    public Object visitSymOpExp(SymOpExp expr) {
        return null;//environment.get(expr.name);
    }

    @Override
    public Object visitOpExp(OpExp expr) {
        return null;
    }


}
