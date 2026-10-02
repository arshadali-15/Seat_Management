--
-- PostgreSQL database dump
--

\restrict BbNPbiJ5dYOxvgaDG9N4Tzxy9PAHqPxA72Uiwp7Y3Z1p72FcVN6q3CFgrWmsygc

-- Dumped from database version 18.4
-- Dumped by pg_dump version 18.4

-- Started on 2026-10-02 12:34:46

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 219 (class 1259 OID 26985)
-- Name: bookings; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bookings (
    booking_id uuid NOT NULL,
    created_at timestamp(6) without time zone,
    created_by character varying(255),
    updated_at timestamp(6) without time zone,
    updated_by character varying(255),
    booking_date date NOT NULL,
    status character varying(255) NOT NULL,
    desk_id uuid NOT NULL,
    user_id uuid NOT NULL,
    CONSTRAINT bookings_status_check CHECK (((status)::text = ANY ((ARRAY['CONFIRMED'::character varying, 'BOOKED'::character varying, 'CANCELLED'::character varying, 'COMPLETED'::character varying])::text[])))
);


ALTER TABLE public.bookings OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 26998)
-- Name: desk; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.desk (
    desk_id uuid NOT NULL,
    created_at timestamp(6) without time zone,
    created_by character varying(255),
    updated_at timestamp(6) without time zone,
    updated_by character varying(255),
    desk_number integer NOT NULL,
    is_active boolean NOT NULL,
    section character varying(255) NOT NULL,
    status character varying(255) NOT NULL,
    CONSTRAINT desk_section_check CHECK (((section)::text = ANY ((ARRAY['CSM'::character varying, 'BOTTOM'::character varying, 'TOP'::character varying, 'RIGHT'::character varying])::text[]))),
    CONSTRAINT desk_status_check CHECK (((status)::text = ANY ((ARRAY['AVAILABLE'::character varying, 'UNAVAILABLE'::character varying, 'BOOKED'::character varying])::text[])))
);


ALTER TABLE public.desk OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 27012)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    user_id uuid NOT NULL,
    created_at timestamp(6) without time zone,
    created_by character varying(255),
    updated_at timestamp(6) without time zone,
    updated_by character varying(255),
    email character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    password_hash character varying(255) NOT NULL,
    role character varying(255) NOT NULL,
    sls_id character varying(255) NOT NULL,
    CONSTRAINT users_role_check CHECK (((role)::text = ANY ((ARRAY['ADMIN'::character varying, 'EMPLOYEE'::character varying])::text[])))
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 5030 (class 0 OID 26985)
-- Dependencies: 219
-- Data for Name: bookings; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.bookings (booking_id, created_at, created_by, updated_at, updated_by, booking_date, status, desk_id, user_id) FROM stdin;
\.


--
-- TOC entry 5031 (class 0 OID 26998)
-- Dependencies: 220
-- Data for Name: desk; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.desk (desk_id, created_at, created_by, updated_at, updated_by, desk_number, is_active, section, status) FROM stdin;
9751bbf0-4c44-479b-a128-65e9e2745e61	\N	\N	\N	\N	1	t	CSM	AVAILABLE
ad682854-a03c-4947-be4f-26a5fda2efdc	\N	\N	\N	\N	2	t	CSM	AVAILABLE
1cf66bb7-eb3d-4b33-8aee-97c758e6c1cb	\N	\N	\N	\N	3	t	CSM	AVAILABLE
d6bcf897-e517-40de-90e7-38f95b49b30c	\N	\N	\N	\N	4	t	CSM	AVAILABLE
e53aee33-d6d3-4d17-b86e-b7b313206309	\N	\N	\N	\N	5	t	CSM	AVAILABLE
3c943bae-d1fc-43b9-8252-81aceb95b656	\N	\N	\N	\N	6	t	CSM	AVAILABLE
12550927-eb68-4c1e-b894-6ab748a84b7e	\N	\N	\N	\N	7	t	CSM	AVAILABLE
4ac932dd-b8f7-42d6-b5fb-e16ba8a95e78	\N	\N	\N	\N	8	t	CSM	AVAILABLE
90bf62e4-2060-4b4a-b9e7-3cdcbc496efe	\N	\N	\N	\N	9	t	CSM	AVAILABLE
c8404edb-65da-4e5c-b8d3-3924cd63c4a0	\N	\N	\N	\N	10	t	CSM	AVAILABLE
676975f2-2742-45e8-be48-2c57ae503c2e	\N	\N	\N	\N	11	t	CSM	AVAILABLE
526473d1-1928-47a2-875f-3a9a4ebe4f20	\N	\N	\N	\N	12	t	CSM	AVAILABLE
b58c7c45-fa0f-4acd-928a-eed26fb1ff84	\N	\N	\N	\N	13	t	CSM	AVAILABLE
85becee0-8441-4f76-a98b-9feee2ce63ac	\N	\N	\N	\N	14	t	CSM	AVAILABLE
0a589bb3-7334-4c29-899f-436fd1a201af	\N	\N	\N	\N	15	t	CSM	AVAILABLE
5150861a-470e-485e-b5b5-97dbed813af8	\N	\N	\N	\N	16	t	CSM	AVAILABLE
d8e86afc-a7ab-4010-82b5-e258d6119627	\N	\N	\N	\N	17	t	CSM	AVAILABLE
013016a6-bd29-4b8c-aca0-c00bda911451	\N	\N	\N	\N	18	t	CSM	AVAILABLE
0914d51b-ad4d-4d70-99f2-28a9ee6b9ea3	\N	\N	\N	\N	19	t	CSM	AVAILABLE
8e8d0b91-49a2-4f1f-b51c-df1d8466d112	\N	\N	\N	\N	20	t	CSM	AVAILABLE
b3238318-e78c-4518-9287-32aa9e84f104	\N	\N	\N	\N	21	t	CSM	AVAILABLE
773749bb-3b77-4281-a993-ecb7ac566303	\N	\N	\N	\N	22	t	CSM	AVAILABLE
79d3c244-4678-4885-9473-87d1d58d2670	\N	\N	\N	\N	23	t	CSM	AVAILABLE
157237ff-0627-4f70-a829-edfcf0801baf	\N	\N	\N	\N	24	t	CSM	AVAILABLE
51cddc6e-2e8b-4b07-9e9a-7ac859ef5540	\N	\N	\N	\N	25	t	CSM	AVAILABLE
f3bfe80c-da00-41f9-97be-43191d56ab66	\N	\N	\N	\N	26	t	CSM	AVAILABLE
b3c70cb3-12b4-45f8-84bd-99f219157866	\N	\N	\N	\N	27	t	CSM	AVAILABLE
9e4aef0b-9efc-48c6-92dd-e56146540c8f	\N	\N	\N	\N	28	t	CSM	AVAILABLE
9be04cae-cfea-4d03-b121-bb8d900f233f	\N	\N	\N	\N	29	t	CSM	AVAILABLE
95a93d2a-7b3a-4f0f-b9d3-040747477bdb	\N	\N	\N	\N	30	t	CSM	AVAILABLE
a2961d20-7c61-47ee-8631-574c75d23920	\N	\N	\N	\N	31	t	CSM	AVAILABLE
46b80e57-a394-44e0-8d43-b1319a4d0925	\N	\N	\N	\N	32	t	CSM	AVAILABLE
72a35f0c-0f1e-401b-a443-64ddc7884f8a	\N	\N	\N	\N	33	t	CSM	AVAILABLE
91638067-0a3e-42de-9cfc-75f0ee2e1fa0	\N	\N	\N	\N	34	t	CSM	AVAILABLE
79c083b3-a280-4c7b-b9d6-12c8f42009ec	\N	\N	\N	\N	35	t	CSM	AVAILABLE
e59b8269-bc6a-49b1-a35e-96dbdde21745	\N	\N	\N	\N	36	t	CSM	AVAILABLE
2e973594-5459-45c5-b19b-e46ee7b295ac	\N	\N	\N	\N	37	t	CSM	AVAILABLE
2ccd87ee-129e-4756-a2be-3c1dd3f9e57f	\N	\N	\N	\N	38	t	CSM	AVAILABLE
226a504e-b2c7-4659-af5b-c8519568a48f	\N	\N	\N	\N	39	t	CSM	AVAILABLE
b99f3655-f8fc-40a1-84f8-682c704368f2	\N	\N	\N	\N	40	t	CSM	AVAILABLE
e3b26533-2445-478d-8018-f4a8a05be56d	\N	\N	\N	\N	41	t	CSM	AVAILABLE
2d7885a9-c611-4f44-a48a-abb9b58c05d7	\N	\N	\N	\N	42	t	CSM	AVAILABLE
b63f3aa0-5bed-4dfc-a20b-9e4c19d25d8d	\N	\N	\N	\N	43	t	CSM	AVAILABLE
253e7780-12dc-450e-9c42-a919ad2b8acf	\N	\N	\N	\N	44	t	CSM	AVAILABLE
f19ff0aa-4505-41a9-9520-0b2f982e947f	\N	\N	\N	\N	45	t	CSM	AVAILABLE
d29a7b4c-7cce-4f73-a1da-c414c0472404	\N	\N	\N	\N	46	t	CSM	AVAILABLE
6145c457-e3e6-4dbb-ab26-c03c8a8f92d9	\N	\N	\N	\N	47	t	CSM	AVAILABLE
47d33f9c-5210-4b39-b89f-5ddfc2a1d655	\N	\N	\N	\N	48	t	CSM	AVAILABLE
5b0c31a6-5a42-4e11-9863-d6059ed4ff73	\N	\N	\N	\N	49	t	CSM	AVAILABLE
c71c5fd5-b356-4e41-a40b-1783d1e774ef	\N	\N	\N	\N	50	t	CSM	AVAILABLE
c813ff40-6355-40a7-9497-9f87cc44085e	\N	\N	\N	\N	51	t	BOTTOM	AVAILABLE
ef4c0c19-2c91-4a3a-b6ac-66e648df1fc7	\N	\N	\N	\N	52	t	BOTTOM	AVAILABLE
c4fae63e-4729-496e-97f8-b699fb4b0ae5	\N	\N	\N	\N	53	t	BOTTOM	AVAILABLE
74726248-663f-45f9-939c-7cea8bd5c7fe	\N	\N	\N	\N	54	t	BOTTOM	AVAILABLE
dddea884-ac93-4839-82fd-95bb5dbb83af	\N	\N	\N	\N	55	t	BOTTOM	AVAILABLE
06ef350b-c8a0-402a-b407-71b9712dc404	\N	\N	\N	\N	56	t	BOTTOM	AVAILABLE
2d06470e-b8c2-4f59-b49e-012449942c35	\N	\N	\N	\N	57	t	BOTTOM	AVAILABLE
91d13aaa-ecbc-4032-96fb-2920125a9f1a	\N	\N	\N	\N	58	t	BOTTOM	AVAILABLE
dbad6342-e0f9-415b-a871-bb5ff7353894	\N	\N	\N	\N	59	t	BOTTOM	AVAILABLE
57354cb3-87de-4a11-9245-be519c704341	\N	\N	\N	\N	60	t	BOTTOM	AVAILABLE
cbdefe6a-ab74-404d-83be-07f86ebc129e	\N	\N	\N	\N	61	t	BOTTOM	AVAILABLE
59e0cd2f-0617-4ae4-bdb7-f75bf42300c8	\N	\N	\N	\N	62	t	BOTTOM	AVAILABLE
f973fbef-02bc-43b6-a45c-8ba4589ee81f	\N	\N	\N	\N	63	t	BOTTOM	AVAILABLE
a8f0e91d-58cf-40e5-950c-cf915fa42654	\N	\N	\N	\N	64	t	BOTTOM	AVAILABLE
1a9b01d2-357b-4982-9bbb-7e8ab808cdf3	\N	\N	\N	\N	65	t	BOTTOM	AVAILABLE
25ec0b7d-8280-42cd-9e0b-e7977618607e	\N	\N	\N	\N	66	t	BOTTOM	AVAILABLE
750c7210-b555-4527-aa31-8fe311c94ec2	\N	\N	\N	\N	67	t	BOTTOM	AVAILABLE
8aaac763-dfd2-4b2d-99e8-3cc71cba01c6	\N	\N	\N	\N	68	t	BOTTOM	AVAILABLE
d3d17779-da27-4f4b-8974-a95779ab1fef	\N	\N	\N	\N	69	t	BOTTOM	AVAILABLE
64694bb7-a092-4efe-809e-e93d0e0dfce1	\N	\N	\N	\N	70	t	BOTTOM	AVAILABLE
dcdc54ec-3818-486e-8b60-e59dba0c9ddf	\N	\N	\N	\N	71	t	BOTTOM	AVAILABLE
d193890f-4675-4330-8d1c-151bb6096480	\N	\N	\N	\N	72	t	BOTTOM	AVAILABLE
04bbed61-ee7a-4cfd-b922-0cf465dc4e53	\N	\N	\N	\N	73	t	BOTTOM	AVAILABLE
3b24c62c-e3b4-47c7-930e-afebe71adf48	\N	\N	\N	\N	74	t	BOTTOM	AVAILABLE
bc4a34f5-e42f-49f0-892d-7a90a99621a3	\N	\N	\N	\N	75	t	BOTTOM	AVAILABLE
f446ce2b-0836-4901-b98a-1f574f9fe685	\N	\N	\N	\N	76	t	BOTTOM	AVAILABLE
7f358242-2e9a-4095-8241-a43765581841	\N	\N	\N	\N	77	t	BOTTOM	AVAILABLE
b1c8a744-f538-43fd-8043-928929fa33e8	\N	\N	\N	\N	78	t	BOTTOM	AVAILABLE
04338e7c-0b28-4512-ba6b-461fb11df882	\N	\N	\N	\N	79	t	BOTTOM	AVAILABLE
f5c254cb-b686-43a1-8c03-681095c47e42	\N	\N	\N	\N	80	t	BOTTOM	AVAILABLE
511ff5d5-9108-481f-bb03-d35999d1c032	\N	\N	\N	\N	81	t	BOTTOM	AVAILABLE
a968da63-b5ec-45c3-96a0-30d2c35570a3	\N	\N	\N	\N	82	t	BOTTOM	AVAILABLE
10072d69-b554-4547-8717-1d4c3729ef63	\N	\N	\N	\N	83	t	BOTTOM	AVAILABLE
c74ffb9a-9c71-4c97-afa0-234c732d751a	\N	\N	\N	\N	84	t	BOTTOM	AVAILABLE
f889491f-f366-4608-baac-d2b2e8994a0f	\N	\N	\N	\N	85	t	BOTTOM	AVAILABLE
2fd6cd7a-e372-4d86-b0cb-e6f7b6283384	\N	\N	\N	\N	86	t	BOTTOM	AVAILABLE
52befa68-fe4d-4d08-9615-b442c10922ae	\N	\N	\N	\N	87	t	BOTTOM	AVAILABLE
2590db57-647b-4918-bb8c-62bc9e389e72	\N	\N	\N	\N	88	t	BOTTOM	AVAILABLE
d6bdd2d4-840b-4779-87c4-95161eb3098e	\N	\N	\N	\N	89	t	BOTTOM	AVAILABLE
4bdf7afe-1b6d-433a-908e-54146b1edc8d	\N	\N	\N	\N	90	t	BOTTOM	AVAILABLE
187ba503-bfa4-433d-8c6b-53233931e72d	\N	\N	\N	\N	91	t	BOTTOM	AVAILABLE
5232ee42-f59f-409e-bfae-b17c4302714c	\N	\N	\N	\N	92	t	BOTTOM	AVAILABLE
a8d25476-351a-41e6-8407-d848a51e9925	\N	\N	\N	\N	93	t	BOTTOM	AVAILABLE
bcf503b3-9523-42ba-a903-ad8f9dbecef5	\N	\N	\N	\N	94	t	BOTTOM	AVAILABLE
942722fa-6cc9-42cc-8b65-f4d83c9bc482	\N	\N	\N	\N	95	t	BOTTOM	AVAILABLE
c9593dca-a1f9-4e54-8771-f2346a8afd72	\N	\N	\N	\N	96	t	BOTTOM	AVAILABLE
9ae92448-aba4-48d8-85d5-1c9ba8320a07	\N	\N	\N	\N	97	t	BOTTOM	AVAILABLE
ac89438f-a370-434a-80ca-d42bc591200f	\N	\N	\N	\N	98	t	BOTTOM	AVAILABLE
1474c304-dfa9-44a5-8c90-0a3157e1f593	\N	\N	\N	\N	99	t	BOTTOM	AVAILABLE
b27ae1f0-bd7c-49b4-8f30-f0ce1dd0ecd8	\N	\N	\N	\N	100	t	BOTTOM	AVAILABLE
f236956a-b2cd-421c-a3f0-13324d4d593c	\N	\N	\N	\N	101	t	BOTTOM	AVAILABLE
111aa0e3-7c32-403a-9999-189e53267af8	\N	\N	\N	\N	102	t	BOTTOM	AVAILABLE
fcfe9625-f847-4963-bd53-743887c93a3e	\N	\N	\N	\N	103	t	BOTTOM	AVAILABLE
3217530c-e5ec-43dc-85f5-4283ad1f2781	\N	\N	\N	\N	104	t	BOTTOM	AVAILABLE
539c0a01-38dc-4137-999b-d20a585f493b	\N	\N	\N	\N	105	t	BOTTOM	AVAILABLE
c89eb457-6bcb-423d-9a77-6b19b8aa9d0f	\N	\N	\N	\N	106	t	BOTTOM	AVAILABLE
dc527d04-3d28-4a85-85a7-563be5457e50	\N	\N	\N	\N	107	t	BOTTOM	AVAILABLE
222f1641-84f4-4c8d-9d51-382e6352fa4d	\N	\N	\N	\N	108	t	BOTTOM	AVAILABLE
cd7eb202-f4c2-4b7e-a161-15bf2e86acdc	\N	\N	\N	\N	109	t	BOTTOM	AVAILABLE
ca74177c-012a-4d1f-b21b-1331a534d4ba	\N	\N	\N	\N	110	t	BOTTOM	AVAILABLE
c96e29bc-0fe3-4bd2-ac29-54d3a4385491	\N	\N	\N	\N	111	t	BOTTOM	AVAILABLE
a2a70307-769f-4113-a7fd-8822536fe369	\N	\N	\N	\N	112	t	BOTTOM	AVAILABLE
aa7d4f08-29d8-4f9e-b160-f6f610e3ebd5	\N	\N	\N	\N	113	t	BOTTOM	AVAILABLE
2bdf265d-9ba6-44c6-abbb-0dc1437472c0	\N	\N	\N	\N	114	t	BOTTOM	AVAILABLE
21890aff-4088-4a8d-97cf-4f29a3303207	\N	\N	\N	\N	115	t	BOTTOM	AVAILABLE
ca0f6698-9ff3-4662-bee7-d677c06c1e12	\N	\N	\N	\N	116	t	BOTTOM	AVAILABLE
74b82c91-7b90-4a18-9331-67b1e07bf1ec	\N	\N	\N	\N	117	t	BOTTOM	AVAILABLE
e96adb43-77b5-4d76-8f7d-f6107b0c38f2	\N	\N	\N	\N	118	t	BOTTOM	AVAILABLE
da28b707-8ae4-4052-84b5-11f5a4d6daf6	\N	\N	\N	\N	119	t	BOTTOM	AVAILABLE
3eff35c7-6ef0-41d6-82c8-b0ace011c646	\N	\N	\N	\N	120	t	BOTTOM	AVAILABLE
02888563-f9e7-46c5-a17b-b563aa160fb4	\N	\N	\N	\N	121	t	BOTTOM	AVAILABLE
93071efe-e8f3-4c08-ae3c-2b41d35bcee3	\N	\N	\N	\N	122	t	BOTTOM	AVAILABLE
a2ca7bb3-6730-4e1d-ab8a-25bb62bbdcb8	\N	\N	\N	\N	123	t	BOTTOM	AVAILABLE
1060f7c2-c370-47f6-926e-241ddceda6b0	\N	\N	\N	\N	124	t	BOTTOM	AVAILABLE
d88ed5ed-ee50-4404-9643-c14d09c8bd3b	\N	\N	\N	\N	125	t	BOTTOM	AVAILABLE
c38e8df2-905f-4429-b2a4-2f5971369fba	\N	\N	\N	\N	126	t	BOTTOM	AVAILABLE
4b15e39a-df53-4858-9f5a-940954fc5a52	\N	\N	\N	\N	127	t	BOTTOM	AVAILABLE
a528f642-a98f-4475-918b-ba8e21ab676d	\N	\N	\N	\N	128	t	BOTTOM	AVAILABLE
4a6f25e4-5c1c-44a9-9c2c-4a2c7e869185	\N	\N	\N	\N	129	t	BOTTOM	AVAILABLE
605ea2e8-6833-4d6a-be08-bacefa4e946f	\N	\N	\N	\N	130	t	BOTTOM	AVAILABLE
91723102-3a84-4492-b692-a09fe90e66a7	\N	\N	\N	\N	131	t	BOTTOM	AVAILABLE
6cd7ec36-34c8-47d6-a8de-6fdfe4125d69	\N	\N	\N	\N	132	t	BOTTOM	AVAILABLE
7b9b4a5f-0771-4984-a55b-ef39f9c4ae35	\N	\N	\N	\N	133	t	BOTTOM	AVAILABLE
406b7690-0933-4dfd-85f5-6fc1dd75e1e7	\N	\N	\N	\N	134	t	BOTTOM	AVAILABLE
39be4d1d-b046-490f-935f-9f95d0cb6c8d	\N	\N	\N	\N	135	t	BOTTOM	AVAILABLE
d16ac16d-d0ba-4029-9b9b-c5767021a6b0	\N	\N	\N	\N	136	t	BOTTOM	AVAILABLE
3e7e1a7f-c6c1-4556-a2ed-2f8989cd6a08	\N	\N	\N	\N	137	t	BOTTOM	AVAILABLE
b36d8a77-7bc5-468f-981e-dc49d66594e3	\N	\N	\N	\N	138	t	BOTTOM	AVAILABLE
c6294956-ce73-419b-bc7b-d9b8afe85a3d	\N	\N	\N	\N	139	t	BOTTOM	AVAILABLE
9af79023-b922-477a-bbf6-e23ee63e19b9	\N	\N	\N	\N	140	t	BOTTOM	AVAILABLE
142c5fed-448b-448f-bf03-c6566548341b	\N	\N	\N	\N	141	t	BOTTOM	AVAILABLE
0e9291db-986f-4d68-b02c-117df061e550	\N	\N	\N	\N	142	t	BOTTOM	AVAILABLE
c65ba2fc-0e62-4260-b117-0321d70bf576	\N	\N	\N	\N	143	t	BOTTOM	AVAILABLE
39ea82cb-cb3b-4faf-9147-1fd6fc90f414	\N	\N	\N	\N	144	t	BOTTOM	AVAILABLE
f08e69dc-3799-4e3b-8ced-e1acac6e4fec	\N	\N	\N	\N	145	t	BOTTOM	AVAILABLE
871d0999-21ee-4860-9d02-509c961d418a	\N	\N	\N	\N	146	t	BOTTOM	AVAILABLE
443606c1-bf74-4032-9b72-9e13cbadd44a	\N	\N	\N	\N	147	t	BOTTOM	AVAILABLE
047dd7b1-8a69-4a9c-a260-ee116105540b	\N	\N	\N	\N	148	t	BOTTOM	AVAILABLE
84992314-7289-40b1-89d8-21fa1a83fcdc	\N	\N	\N	\N	149	t	BOTTOM	AVAILABLE
a87166ae-d183-4fa7-9401-372035738e37	\N	\N	\N	\N	150	t	BOTTOM	AVAILABLE
6f6f297d-c7bc-436f-a0e4-9372b28e3060	\N	\N	\N	\N	151	t	RIGHT	AVAILABLE
418df96f-c954-49c7-8b98-d70d2889e672	\N	\N	\N	\N	152	t	RIGHT	AVAILABLE
eb0689c5-2852-4b56-aba2-247c48ebb415	\N	\N	\N	\N	153	t	RIGHT	AVAILABLE
bf0a3e9d-5700-4b74-ae06-7816b69fb345	\N	\N	\N	\N	154	t	RIGHT	AVAILABLE
d7f1a778-3212-4f17-8851-b326b4a19105	\N	\N	\N	\N	155	t	RIGHT	AVAILABLE
ceea9b22-285b-4086-875b-b99007ea003c	\N	\N	\N	\N	156	t	RIGHT	AVAILABLE
4c081721-75a8-4eb2-96ce-f8fc11b998b5	\N	\N	\N	\N	157	t	RIGHT	AVAILABLE
86ad438c-46a4-4798-825b-12183e87e552	\N	\N	\N	\N	158	t	RIGHT	AVAILABLE
76080d42-b592-4e49-b2ab-ebe5105cb6af	\N	\N	\N	\N	159	t	RIGHT	AVAILABLE
b6423048-5778-4d3f-8345-f8f164433b0a	\N	\N	\N	\N	160	t	RIGHT	AVAILABLE
401814ff-5d98-44fb-8fc0-e20f59bcedb2	\N	\N	\N	\N	161	t	RIGHT	AVAILABLE
f06da806-562f-4b2a-a207-39f83eafb930	\N	\N	\N	\N	162	t	RIGHT	AVAILABLE
78651a8c-f8cf-46ac-9393-1a7f64775971	\N	\N	\N	\N	163	t	RIGHT	AVAILABLE
d7bf628e-65ba-460c-a628-2e395e5faf80	\N	\N	\N	\N	164	t	RIGHT	AVAILABLE
99386311-29dd-486c-afb4-604e05e350f5	\N	\N	\N	\N	165	t	RIGHT	AVAILABLE
ca7067cb-e9fd-41f3-9f07-9b2456347d82	\N	\N	\N	\N	166	t	RIGHT	AVAILABLE
2239e374-1043-4d2e-82cf-846fac504a2b	\N	\N	\N	\N	167	t	RIGHT	AVAILABLE
808d54ef-c2b9-4df9-90b8-14f63ba50b47	\N	\N	\N	\N	168	t	RIGHT	AVAILABLE
efb6a1e7-fe05-48ca-b8da-d9a079176069	\N	\N	\N	\N	169	t	RIGHT	AVAILABLE
408b4296-a5b1-4e29-934b-a4b87b011d5c	\N	\N	\N	\N	170	t	RIGHT	AVAILABLE
fdd3a7f7-614b-4386-8414-656a4b39d7eb	\N	\N	\N	\N	171	t	RIGHT	AVAILABLE
edb0460d-8892-40a5-850c-f480bc437c8f	\N	\N	\N	\N	172	t	RIGHT	AVAILABLE
9973c40d-0dd0-4e5c-b60a-b1d024695a63	\N	\N	\N	\N	173	t	RIGHT	AVAILABLE
b265fa35-21d8-49cd-958c-40d8a1778f33	\N	\N	\N	\N	174	t	RIGHT	AVAILABLE
4add9ed6-9a48-4d19-9859-02a7b7e9eb81	\N	\N	\N	\N	175	t	RIGHT	AVAILABLE
95aec1ef-b805-45f2-992b-ad22447f3b46	\N	\N	\N	\N	176	t	RIGHT	AVAILABLE
f1a1bfae-1cfb-4d3c-820b-28df17b80dc6	\N	\N	\N	\N	177	t	RIGHT	AVAILABLE
d161af10-4bf2-484f-9c06-e4efe62d73b8	\N	\N	\N	\N	178	t	RIGHT	AVAILABLE
16054971-5b71-4dff-9a00-9c52ddba2911	\N	\N	\N	\N	179	t	RIGHT	AVAILABLE
2bd45375-359a-48f6-bffd-67d75471360d	\N	\N	\N	\N	180	t	RIGHT	AVAILABLE
dded4543-1faf-4fe2-b637-f1582ea5a107	\N	\N	\N	\N	181	t	RIGHT	AVAILABLE
03f4b750-ce26-4d6b-9f35-5faa7ef9663c	\N	\N	\N	\N	182	t	RIGHT	AVAILABLE
b1759f08-b397-4767-a747-22133ff1a7e8	\N	\N	\N	\N	183	t	RIGHT	AVAILABLE
02435eb7-9777-480a-9cfd-4df88f4f6b18	\N	\N	\N	\N	184	t	RIGHT	AVAILABLE
51067da0-94ae-4ab6-8da6-c5841fe02adb	\N	\N	\N	\N	185	t	RIGHT	AVAILABLE
c0338e6f-8add-4d6f-9829-dca7accbf907	\N	\N	\N	\N	186	t	RIGHT	AVAILABLE
d70de7dd-c8ae-4a68-bd42-d678614c0597	\N	\N	\N	\N	187	t	RIGHT	AVAILABLE
982b4cef-d12a-4f65-92e5-f112a9d2234c	\N	\N	\N	\N	188	t	RIGHT	AVAILABLE
33925f04-8569-4156-955b-d41b4c73b886	\N	\N	\N	\N	189	t	RIGHT	AVAILABLE
436ed3e9-6093-4bea-9b1f-c94c8862b2d3	\N	\N	\N	\N	190	t	RIGHT	AVAILABLE
6fbcadc4-cbf6-4d37-bdb4-e1363fdd4080	\N	\N	\N	\N	191	t	RIGHT	AVAILABLE
7548c8b5-ee92-4f39-9292-52ecf284ad25	\N	\N	\N	\N	192	t	RIGHT	AVAILABLE
f7024ed4-1c79-4cc2-8a82-8ff22069dfc5	\N	\N	\N	\N	193	t	RIGHT	AVAILABLE
85738e89-ff88-4e7c-a8c1-cc24a8bdf66b	\N	\N	\N	\N	194	t	RIGHT	AVAILABLE
57e89fe1-566d-4b95-baaa-f16b24c9d506	\N	\N	\N	\N	195	t	RIGHT	AVAILABLE
1b5c9dc9-5b63-497c-b3e8-725c28907092	\N	\N	\N	\N	196	t	RIGHT	AVAILABLE
20c48f5d-5d3f-40b8-aa90-a872625dacb2	\N	\N	\N	\N	197	t	RIGHT	AVAILABLE
71f18ae5-cee8-4db2-b709-5c448b4c4007	\N	\N	\N	\N	198	t	RIGHT	AVAILABLE
2aca25f3-0485-4b63-9346-4636d2c744a4	\N	\N	\N	\N	199	t	RIGHT	AVAILABLE
9453f65f-063f-4db4-9fce-6c230b618ffb	\N	\N	\N	\N	200	t	RIGHT	AVAILABLE
cc79dbde-31c4-4180-b2de-62ad08681bcb	\N	\N	\N	\N	201	t	TOP	AVAILABLE
ec505ec8-0d3c-48b0-a54d-0f4c9e306a0c	\N	\N	\N	\N	202	t	TOP	AVAILABLE
4012da12-d39f-40c5-9700-3b2cd6b94ff7	\N	\N	\N	\N	203	t	TOP	AVAILABLE
ab710083-6eb7-46b8-87f5-09363dfd7ba0	\N	\N	\N	\N	204	t	TOP	AVAILABLE
d5b450f6-752e-46ba-a240-de8f8868c511	\N	\N	\N	\N	205	t	TOP	AVAILABLE
40b09294-cc52-4bd8-b539-408a4ee0292b	\N	\N	\N	\N	206	t	TOP	AVAILABLE
84ef6878-5de6-406b-89ee-6bc2bcdec109	\N	\N	\N	\N	207	t	TOP	AVAILABLE
384bb3a6-0aa6-4a87-8044-609735ba2807	\N	\N	\N	\N	208	t	TOP	AVAILABLE
04c0b81b-390c-4484-bee5-f6b89ff07cf9	\N	\N	\N	\N	209	t	TOP	AVAILABLE
7fa52c8c-f020-45ad-b514-3333a10eac3e	\N	\N	\N	\N	210	t	TOP	AVAILABLE
eb051108-e0bd-4e02-9497-fac4f46b4d0a	\N	\N	\N	\N	211	t	TOP	AVAILABLE
07c6395f-d7dc-4e7e-afa4-aaccb518867b	\N	\N	\N	\N	212	t	TOP	AVAILABLE
9b598da3-6c2c-40c9-9dce-1b6d45e9ed27	\N	\N	\N	\N	213	t	TOP	AVAILABLE
440b1312-28f2-4803-a669-4db749ced9aa	\N	\N	\N	\N	214	t	TOP	AVAILABLE
92a070f3-c6fd-49e1-a251-1b79189908da	\N	\N	\N	\N	215	t	TOP	AVAILABLE
1750b288-8d29-43a2-b0a4-1b119d07e87a	\N	\N	\N	\N	216	t	TOP	AVAILABLE
d3f18f12-c06b-40b2-bd1a-b105d3274aee	\N	\N	\N	\N	217	t	TOP	AVAILABLE
4f2c9e6a-fdef-4752-8bb7-23a2e5c4ebad	\N	\N	\N	\N	218	t	TOP	AVAILABLE
614e34b1-4b75-4635-b946-c36d3cf528d4	\N	\N	\N	\N	219	t	TOP	AVAILABLE
9a61b494-6b3e-4af5-b9cd-1221af4b86a3	\N	\N	\N	\N	220	t	TOP	AVAILABLE
0b9877a1-77be-4051-ac72-03bcc4ac4c36	\N	\N	\N	\N	221	t	TOP	AVAILABLE
5cf46da2-a3e6-48ea-9693-28baac3e68d6	\N	\N	\N	\N	222	t	TOP	AVAILABLE
cf100697-3052-4aff-acc1-c0d401fec044	\N	\N	\N	\N	223	t	TOP	AVAILABLE
ff0185d3-1307-4186-8301-7dba1ce08622	\N	\N	\N	\N	224	t	TOP	AVAILABLE
68fabf4e-3d02-4871-98b4-1a0c528c8928	\N	\N	\N	\N	225	t	TOP	AVAILABLE
c4082a8e-20d4-435c-a281-306765a751e7	\N	\N	\N	\N	226	t	TOP	AVAILABLE
019d49c3-35f7-4d07-8383-3758fe7c78dc	\N	\N	\N	\N	227	t	TOP	AVAILABLE
bd8d3d3c-a98c-4b84-bef0-6e15b1c1b40a	\N	\N	\N	\N	228	t	TOP	AVAILABLE
a5325b9e-06bd-4e6c-b51d-54d8cd1b3f2b	\N	\N	\N	\N	229	t	TOP	AVAILABLE
b95fbcbb-6eff-4e2b-82b1-7f8eba19af0a	\N	\N	\N	\N	230	t	TOP	AVAILABLE
\.


--
-- TOC entry 5032 (class 0 OID 27012)
-- Dependencies: 221
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (user_id, created_at, created_by, updated_at, updated_by, email, name, password_hash, role, sls_id) FROM stdin;
452c0b3c-66ca-467a-b466-e0f82b053a8c	\N	\N	\N	\N	admin1@example.com	Admin 1	$2a$10$yuZtQlLhMkDqqoEFhom8oerCA.JsWglPGCIZvvpKSGllS9CWS2LQG	ADMIN	SLS0001
13ab4262-cefc-45cc-a8c0-bc5726d7369a	\N	\N	\N	\N	admin2@example.com	Admin 2	$2a$10$yuZtQlLhMkDqqoEFhom8oerCA.JsWglPGCIZvvpKSGllS9CWS2LQG	ADMIN	SLS0002
d04d52cd-6506-43b3-8a8d-2ee296b389a4	\N	\N	\N	\N	admin3@example.com	Admin 3	$2a$10$yuZtQlLhMkDqqoEFhom8oerCA.JsWglPGCIZvvpKSGllS9CWS2LQG	ADMIN	SLS0003
d354eb7f-a1b1-4360-bee1-cfa30d333096	\N	\N	\N	\N	admin4@example.com	Admin 4	$2a$10$yuZtQlLhMkDqqoEFhom8oerCA.JsWglPGCIZvvpKSGllS9CWS2LQG	ADMIN	SLS0004
a3d7fd65-fe84-471c-965a-f9d52a66c706	\N	\N	\N	\N	admin5@example.com	Admin 5	$2a$10$yuZtQlLhMkDqqoEFhom8oerCA.JsWglPGCIZvvpKSGllS9CWS2LQG	ADMIN	SLS0005
0ee34962-6e1e-41c6-b4e4-470ae142a237	\N	\N	\N	\N	employee1@example.com	Employee 1	$2a$10$yuZtQlLhMkDqqoEFhom8oerCA.JsWglPGCIZvvpKSGllS9CWS2LQG	EMPLOYEE	SLS0006
4eaa484c-274b-404c-8d7f-1d67d5b45ab8	\N	\N	\N	\N	employee2@example.com	Employee 2	$2a$10$yuZtQlLhMkDqqoEFhom8oerCA.JsWglPGCIZvvpKSGllS9CWS2LQG	EMPLOYEE	SLS0007
93cf06b7-32ef-49e6-896c-0e882da84edd	\N	\N	\N	\N	employee3@example.com	Employee 3	$2a$10$yuZtQlLhMkDqqoEFhom8oerCA.JsWglPGCIZvvpKSGllS9CWS2LQG	EMPLOYEE	SLS0008
fd03fdc3-f297-4d91-8f97-73317beecf35	\N	\N	\N	\N	employee4@example.com	Employee 4	$2a$10$yuZtQlLhMkDqqoEFhom8oerCA.JsWglPGCIZvvpKSGllS9CWS2LQG	EMPLOYEE	SLS0009
cf2049cb-3be5-4516-b261-4b5d84694e7c	\N	\N	\N	\N	employee5@example.com	Employee 5	$2a$10$yuZtQlLhMkDqqoEFhom8oerCA.JsWglPGCIZvvpKSGllS9CWS2LQG	EMPLOYEE	SLS0010
de473ace-635f-4046-944a-94fa7863cf60	\N	\N	\N	\N	employee6@example.com	Employee 6	$2a$10$yuZtQlLhMkDqqoEFhom8oerCA.JsWglPGCIZvvpKSGllS9CWS2LQG	EMPLOYEE	SLS0011
2edf3a8a-376f-45e2-93d0-f57fd238eda1	\N	\N	\N	\N	employee7@example.com	Employee 7	$2a$10$yuZtQlLhMkDqqoEFhom8oerCA.JsWglPGCIZvvpKSGllS9CWS2LQG	EMPLOYEE	SLS0012
08d334da-040a-42b6-b6a1-3c39d8dba6df	\N	\N	\N	\N	employee8@example.com	Employee 8	$2a$10$yuZtQlLhMkDqqoEFhom8oerCA.JsWglPGCIZvvpKSGllS9CWS2LQG	EMPLOYEE	SLS0013
870dcb25-9ecc-4658-aa7b-b2690e401a76	\N	\N	\N	\N	employee9@example.com	Employee 9	$2a$10$yuZtQlLhMkDqqoEFhom8oerCA.JsWglPGCIZvvpKSGllS9CWS2LQG	EMPLOYEE	SLS0014
cf84cc53-eb24-496e-9668-1726bdfc27e0	\N	\N	\N	\N	employee10@example.com	Employee 10	$2a$10$yuZtQlLhMkDqqoEFhom8oerCA.JsWglPGCIZvvpKSGllS9CWS2LQG	EMPLOYEE	SLS0015
5d781ef5-8dcb-48fe-86b8-2f109dc597c4	\N	\N	\N	\N	employee11@example.com	Employee 11	$2a$10$yuZtQlLhMkDqqoEFhom8oerCA.JsWglPGCIZvvpKSGllS9CWS2LQG	EMPLOYEE	SLS0016
4fb49db1-7f4e-4c53-9c8a-bf553a4bdf0c	\N	\N	\N	\N	employee12@example.com	Employee 12	$2a$10$yuZtQlLhMkDqqoEFhom8oerCA.JsWglPGCIZvvpKSGllS9CWS2LQG	EMPLOYEE	SLS0017
aebcacc5-5807-40fc-a2d3-e47afc240786	\N	\N	\N	\N	employee13@example.com	Employee 13	$2a$10$yuZtQlLhMkDqqoEFhom8oerCA.JsWglPGCIZvvpKSGllS9CWS2LQG	EMPLOYEE	SLS0018
3ad785d4-9942-464e-a942-7085438332ff	\N	\N	\N	\N	employee14@example.com	Employee 14	$2a$10$yuZtQlLhMkDqqoEFhom8oerCA.JsWglPGCIZvvpKSGllS9CWS2LQG	EMPLOYEE	SLS0019
ce95be71-e78d-43ce-af96-84384eb9ea7a	\N	\N	\N	\N	employee16@example.com	Employee 16	$2a$10$yuZtQlLhMkDqqoEFhom8oerCA.JsWglPGCIZvvpKSGllS9CWS2LQG	EMPLOYEE	SLS0021
21ef71dd-3d7c-4760-b548-34ce8d4b4269	\N	\N	\N	\N	employee17@example.com	Employee 17	$2a$10$yuZtQlLhMkDqqoEFhom8oerCA.JsWglPGCIZvvpKSGllS9CWS2LQG	EMPLOYEE	SLS0022
cd12e327-9ead-438d-8b84-7f8e15115be1	\N	\N	\N	\N	employee18@example.com	Employee 18	$2a$10$yuZtQlLhMkDqqoEFhom8oerCA.JsWglPGCIZvvpKSGllS9CWS2LQG	EMPLOYEE	SLS0023
5ebdf4d2-73ae-49ba-b25e-5fae2e644c37	\N	\N	\N	\N	employee19@example.com	Employee 19	$2a$10$yuZtQlLhMkDqqoEFhom8oerCA.JsWglPGCIZvvpKSGllS9CWS2LQG	EMPLOYEE	SLS0024
a6708473-037a-4e26-88e9-5b2d84f1f040	\N	\N	\N	\N	employee20@example.com	Employee 20	$2a$10$yuZtQlLhMkDqqoEFhom8oerCA.JsWglPGCIZvvpKSGllS9CWS2LQG	EMPLOYEE	SLS0025
6ce902ac-c623-44b5-8380-d2d33b965bbb	\N	\N	2026-09-28 17:18:27.910826	\N	employee15@example.com	Employee 15	$2a$10$5Gl5DD5GV9NnaQlCyZ0fhO0R3vkol4Sym2fdQ85/3kTieNZ1NglBy	EMPLOYEE	SLS0020
ad5c8da6-4c76-4379-bfa7-2ccbbbf25ba9	2026-09-28 17:39:54.7714	admin1@example.com	2026-09-28 17:39:54.7714	admin1@example.com	asd@mail.sa	asd	$2a$10$HVR9TqtKv.wrizgsX5KwmusA1eciFXoiJC16mDaWY8WI/bAGR132u	EMPLOYEE	SLS1
79c7cb0b-088d-4794-a23b-8a373c9973fa	2026-09-28 17:42:26.701421	admin1@example.com	2026-09-28 17:42:26.701421	admin1@example.com	sdd@mailasd.fhfg	dsf	$2a$10$2xVL/ndSFyIFQ7bXZkwdK.7jpa1xHo4grNnEVoSFYBix3Uk4n76Fy	EMPLOYEE	dsff
\.


--
-- TOC entry 4868 (class 2606 OID 26997)
-- Name: bookings bookings_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bookings
    ADD CONSTRAINT bookings_pkey PRIMARY KEY (booking_id);


--
-- TOC entry 4872 (class 2606 OID 27011)
-- Name: desk desk_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.desk
    ADD CONSTRAINT desk_pkey PRIMARY KEY (desk_id);


--
-- TOC entry 4876 (class 2606 OID 27033)
-- Name: users uk3mdqbbvayv8grsmfyoqacamm4; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk3mdqbbvayv8grsmfyoqacamm4 UNIQUE (sls_id);


--
-- TOC entry 4878 (class 2606 OID 27031)
-- Name: users uk6dotkott2kjsp8vw4d0m25fb7; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);


--
-- TOC entry 4870 (class 2606 OID 27027)
-- Name: bookings uk_booking_desk_date; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bookings
    ADD CONSTRAINT uk_booking_desk_date UNIQUE (desk_id, booking_date);


--
-- TOC entry 4874 (class 2606 OID 27029)
-- Name: desk ukqx46ybxwuelml6q1by1ovg77j; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.desk
    ADD CONSTRAINT ukqx46ybxwuelml6q1by1ovg77j UNIQUE (desk_number);


--
-- TOC entry 4880 (class 2606 OID 27025)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);


--
-- TOC entry 4881 (class 2606 OID 27034)
-- Name: bookings fk4k2nxnn2n501dbt5t47xgxq8m; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bookings
    ADD CONSTRAINT fk4k2nxnn2n501dbt5t47xgxq8m FOREIGN KEY (desk_id) REFERENCES public.desk(desk_id);


--
-- TOC entry 4882 (class 2606 OID 27039)
-- Name: bookings fkeyog2oic85xg7hsu2je2lx3s6; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bookings
    ADD CONSTRAINT fkeyog2oic85xg7hsu2je2lx3s6 FOREIGN KEY (user_id) REFERENCES public.users(user_id);


-- Completed on 2026-10-02 12:34:46

--
-- PostgreSQL database dump complete
--

\unrestrict BbNPbiJ5dYOxvgaDG9N4Tzxy9PAHqPxA72Uiwp7Y3Z1p72FcVN6q3CFgrWmsygc

